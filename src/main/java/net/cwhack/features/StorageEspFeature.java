package net.cwhack.features;

import com.mojang.blaze3d.systems.RenderSystem;
import net.cwhack.events.RenderListener;
import net.cwhack.feature.Feature;
import net.cwhack.mixinterface.IWorld;
import net.cwhack.utils.RenderUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.BlockEntityTickInvoker;
import org.lwjgl.opengl.GL11;

import static net.cwhack.CwHack.MC;

public class StorageEspFeature extends Feature implements RenderListener
{

	public StorageEspFeature()
	{
		super("StorageESP", "esp for containers");
	}

	@Override
	protected void onEnable()
	{
		eventManager.add(RenderListener.class, this);
	}

	@Override
	protected void onDisable()
	{
		eventManager.remove(RenderListener.class, this);
	}

	@Override
	public void onRender(RenderEvent event)
	{
		MatrixStack matrixStack = event.getMatrixStack();
		double partialTicks = event.getPartialTicks();

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		matrixStack.push();
		RenderUtils.applyRegionalRenderOffset(matrixStack);

		BlockPos camPos = RenderUtils.getCameraBlockPos();
		int regionX = (camPos.getX() >> 9) * 512;
		int regionZ = (camPos.getZ() >> 9) * 512;

		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS,
				VertexFormats.POSITION);
		RenderSystem.setShader(GameRenderer::getPositionShader);
		RenderSystem.setShaderColor(0.4f, 0.4f, 1, 0.5f);
		for (BlockEntityTickInvoker blockEntityTicker : ((IWorld) MC.world).getBlockEntityTickers())
		{
			BlockPos pos = blockEntityTicker.getPos();
			BlockEntity blockEntity = MC.world.getBlockEntity(pos);
			if (blockEntity instanceof LockableContainerBlockEntity)
			{
				Box bb = blockEntity.getCachedState().getOutlineShape(MC.world, pos).getBoundingBox();

				matrixStack.push();
				matrixStack.translate(
						pos.getX() - regionX,
						pos.getY(),
						pos.getZ() - regionZ);

				RenderUtils.fillBox(bufferBuilder, bb, matrixStack);

				matrixStack.pop();
			}
		}

		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);

		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION);
		for (BlockEntityTickInvoker blockEntityTicker : ((IWorld) MC.world).getBlockEntityTickers())
		{
			BlockPos pos = blockEntityTicker.getPos();
			BlockEntity blockEntity = MC.world.getBlockEntity(pos);
			if (blockEntity instanceof LockableContainerBlockEntity)
			{
				Box bb = blockEntity.getCachedState().getOutlineShape(MC.world, pos).getBoundingBox();

				matrixStack.push();
				matrixStack.translate(pos.getX() - regionX, pos.getY(), pos.getZ() - regionZ);
				Matrix4f matrix1 = matrixStack.peek().getPositionMatrix();

				Vec3d end = bb.getCenter();

				Vec3d start = RenderUtils.getRenderLookVec(partialTicks).subtract(Vec3d.of(pos)).add(RenderUtils.getCameraPos());
				bufferBuilder.vertex(matrix1, (float) start.x, (float) start.y, (float) start.z).next();
				bufferBuilder.vertex(matrix1, (float) end.x, (float) end.y, (float) end.z).next();

				matrixStack.pop();
			}
		}

		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);

		matrixStack.pop();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
}
