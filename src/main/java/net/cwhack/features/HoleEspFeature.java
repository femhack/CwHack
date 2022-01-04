package net.cwhack.features;

import com.mojang.blaze3d.systems.RenderSystem;
import net.cwhack.events.RenderListener;
import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;
import net.cwhack.utils.BlockUtils;
import net.cwhack.utils.HoleUtils;
import net.cwhack.utils.RenderUtils;
import net.cwhack.utils.RotationUtils;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HoleEspFeature extends Feature implements UpdateListener, RenderListener
{

	private ArrayList<BlockPos> deepHoles;
	private ArrayList<BlockPos> shallowHoles;

	public HoleEspFeature()
	{
		super("HoleESP", "Highlight holes on bedrock");
	}

	@Override
	protected void onEnable()
	{
		eventManager.add(UpdateListener.class, this);
		eventManager.add(RenderListener.class, this);
	}

	@Override
	protected void onDisable()
	{
		eventManager.remove(UpdateListener.class, this);
		eventManager.remove(RenderListener.class, this);
	}

	@Override
	public void onUpdate()
	{
		deepHoles = getPossibleHoles().filter(HoleUtils::isFullySurrounded)
				.collect(Collectors.toCollection(ArrayList::new));

		shallowHoles = getPossibleHoles().filter(pos -> HoleUtils.isSurrounded(pos) && HoleUtils.isFullySurrounded(pos))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public void onRender(RenderEvent event)
	{
		if (deepHoles != null)
			deepHoles.forEach(pos -> drawHole(event.getMatrixStack(), pos, new Vec3f(0.4f, 0.4f, 1.0f)));
		if (shallowHoles != null)
			shallowHoles.forEach(pos -> drawHole(event.getMatrixStack(), pos, new Vec3f(1.0f, 0.4f, 0.4f)));
	}

	private Stream<BlockPos> getPossibleHoles()
	{
		return BlockUtils.getAllInBoxStream(RotationUtils.getEyesBlockPos().add(-16, -8, -16),
				RotationUtils.getEyesBlockPos().add(16, 8, 16))
//				.filter(pos -> pos.getY() >= 0 && pos.getY() <= 4)
				.filter(pos -> !BlockUtils.hasBlock(pos));
	}

	private void drawHole(MatrixStack matrixStack, BlockPos pos, Vec3f color)
	{
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		matrixStack.push();
		RenderUtils.applyRegionalRenderOffset(matrixStack);
		BlockPos blockPos = RenderUtils.getCameraBlockPos();
		int regionX = (blockPos.getX() >> 9) * 512;
		int regionZ = (blockPos.getZ() >> 9) * 512;

		matrixStack.translate(pos.getX() - regionX,
				pos.getY(), pos.getZ() - regionZ);

		matrixStack.scale(1.0f, 0.125f, 1.0f);

		RenderSystem.setShader(GameRenderer::getPositionShader);

		RenderSystem.setShaderColor(color.getX(), color.getY(), color.getZ(), 0.2f);
		RenderUtils.drawSolidBox(new Box(pos.ORIGIN), matrixStack);

		matrixStack.pop();

		RenderSystem.setShaderColor(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
}
