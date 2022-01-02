package net.cwhack.features;

import com.mojang.blaze3d.systems.RenderSystem;
import net.cwhack.events.RenderListener;
import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;
import net.cwhack.utils.RenderUtils;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.*;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static net.cwhack.CwHack.CWHACK;
import static net.cwhack.CwHack.MC;

public class PlayerEspFeature extends Feature implements UpdateListener, RenderListener
{

	private ArrayList<PlayerEntity> players;

	public PlayerEspFeature()
	{
		super("PlayerESP", "render a tracer and a box at each players");
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
	public void onRender(RenderEvent event)
	{
		if (players == null)
			return;

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

		for (PlayerEntity e : players)
		{
			matrixStack.push();
			matrixStack.translate(
					MathHelper.lerp(partialTicks, e.prevX, e.getX()) - regionX,
					MathHelper.lerp(partialTicks, e.prevY, e.getY()),
					MathHelper.lerp(partialTicks, e.prevZ, e.getZ()) - regionZ);

			matrixStack.scale(e.getWidth(), e.getHeight(), e.getWidth());

			RenderSystem.setShaderColor(0.4f, 0.4f, 1, 0.5f);
			if (CWHACK.getFriendList().isFriend(e))
				RenderSystem.setShaderColor(0.4f, 1.0f, 0.4f, 0.5f);

			Box bb = new Box(-0.5, 0, -0.5, 0.5, 1, 0.5);
			RenderUtils.drawOutlinedBox(bb, matrixStack);

			matrixStack.pop();
		}

		Vec3d start = RenderUtils.getRenderLookVec(partialTicks).add(RenderUtils.getCameraPos());

		Matrix4f matrix = matrixStack.peek().getModel();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

		for (PlayerEntity e : players)
		{
			Vec3d end = e.getBoundingBox().getCenter()
					.subtract(new Vec3d(e.getX(), e.getY(), e.getZ())
							.subtract(e.prevX, e.prevY, e.prevZ)
							.multiply(1 - partialTicks));

			float f = 4.0f / MC.player.distanceTo(e);

			Vec3d red = new Vec3d(1.0, 0.0, 0.0);
			Vec3d blue = new Vec3d(0.4, 0.4, 1.0);

			Vec3d color = red.multiply(f).add(blue.multiply(1.0 - f));

			RenderSystem.setShaderColor((float) color.x, (float) color.y, (float) color.z, 0.5f);
			if (CWHACK.getFriendList().isFriend(e))
				RenderSystem.setShaderColor(0.4f, 1.0f, 0.4f, 0.5f);

			bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION);

			bufferBuilder.vertex(matrix, (float)start.x - regionX,
					(float)start.y, (float)start.z - regionZ).next();
			bufferBuilder.vertex(matrix, (float)end.x - regionX, (float)end.y,
					(float)end.z - regionZ).next();

			bufferBuilder.end();
			BufferRenderer.draw(bufferBuilder);
		}

		matrixStack.pop();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}

	@Override
	public void onUpdate()
	{
		players = MC.world.getPlayers().parallelStream()
				.filter(e -> e != MC.player)
				.filter(e -> !e.isRemoved())
				.filter(e -> e.getHealth() > 0)
				.collect(Collectors.toCollection(ArrayList::new));
	}
}
