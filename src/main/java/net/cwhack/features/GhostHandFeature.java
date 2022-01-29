package net.cwhack.features;

import com.mojang.blaze3d.systems.RenderSystem;
import net.cwhack.events.GetOutlineShapeListener;
import net.cwhack.events.RenderListener;
import net.cwhack.feature.Feature;
import net.cwhack.utils.BlockUtils;
import net.cwhack.utils.RenderUtils;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShapes;
import org.lwjgl.opengl.GL11;

import static net.cwhack.CwHack.MC;

public class GhostHandFeature extends Feature implements GetOutlineShapeListener, RenderListener
{

	public GhostHandFeature()
	{
		super("GhostHand", "loot jewer Lolllll");
	}

	@Override
	protected void onEnable()
	{
		eventManager.add(GetOutlineShapeListener.class, this);
		eventManager.add(RenderListener.class, this);
	}

	@Override
	protected void onDisable()
	{
		eventManager.remove(GetOutlineShapeListener.class, this);
		eventManager.remove(RenderListener.class, this);
	}

	@Override
	public void onGetOutlineShape(GetOutlineShapeEvent event)
	{
		if (event.getContext() != ShapeContext.absent() && event.getView() == MC.world && !BlockUtils.isContainer(event.getPos()))
			event.setReturnValue(VoxelShapes.empty());
	}

	@Override
	public void onRender(RenderEvent event)
	{
		if (MC.crosshairTarget == null)
			return;
		if (MC.crosshairTarget.getType() != HitResult.Type.BLOCK)
			return;
		BlockPos block = ((BlockHitResult) MC.crosshairTarget).getBlockPos();

		RenderSystem.setShaderColor(0.4f, 0.4f, 1, 1.0f);

		if (BlockUtils.isBlock(Blocks.CHEST, block) || BlockUtils.isBlock(Blocks.TRAPPED_CHEST, block))
		{
			if (ChestBlock.isChestBlocked(MC.world, block))
				RenderSystem.setShaderColor(1, 0.4f, 0.4f, 1.0f);
		}

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

		matrixStack.push();
		matrixStack.translate(
				block.getX() - regionX,
				block.getY(),
				block.getZ() - regionZ);
		RenderUtils.drawSolidBox(BlockUtils.getBlockState(block).getOutlineShape(MC.world, block).getBoundingBox(), matrixStack);
		matrixStack.pop();

		matrixStack.pop();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
}
