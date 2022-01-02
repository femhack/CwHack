package net.cwhack;

import net.cwhack.utils.BlockUtils;
import net.cwhack.utils.RotationUtils;
import net.minecraft.block.BlockState;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

import static net.cwhack.CwHack.CWHACK;
import static net.cwhack.CwHack.MC;
import static net.cwhack.utils.BlockUtils.hasBlock;

public enum BlockPlacer
{
	;

	public static boolean placeBlock(BlockPos pos)
	{
		return placeBlock(pos, BlockUtils.getDefaultBlockState());
	}

	public static boolean placeBlock(BlockPos pos, BlockState state)
	{
		// if block is replaceable
//		if (hasBlock(pos) && BlockUtils.isBlockReplaceable(pos))
//		{
//			BlockState blockToReplace = BlockUtils.getBlockState(pos);
//			Vec3d center = blockToReplace.getOutlineShape(MC.world, pos)
//					.getBoundingBox().getCenter();
//
//
//
//			// fake rotation
//			CWHACK.getRotationFaker().setServerLookPos(center);
//
//			// get raycast result
//			BlockHitResult hitResult = BlockUtils.serverRaycastBlock(pos);
//			ActionResult result = MC.interactionManager.interactBlock(MC.player, MC.world, Hand.MAIN_HAND, hitResult);
//
//			// swing hand
//			boolean succeed = result == ActionResult.SUCCESS;
//			if (succeed)
//				MC.player.swingHand(Hand.MAIN_HAND);
//
//			// return
//			return succeed;
//		}

		// if there has already been a block
		if (hasBlock(pos))
			return false;

		if (!BlockUtils.canPlace(state, pos))
			return false;

		// if there is no clickable neighbors
		ArrayList<BlockPos> neighbors = BlockUtils.getClickableNeighbors(pos);
		if (neighbors.isEmpty())
			return false;

		// find the correct neighbor to click on
		BlockPos neighborToClick = null;
		Direction directionToClick = null;
		Vec3d faceCenterToClick = null;
		for (BlockPos neighbor : neighbors)
		{
			BlockState block = BlockUtils.getBlockState(neighbor);
			Direction correctFace = null;

			// iterate through 6 faces to find the correct face
			for (Direction face : Direction.values())
			{
				if (pos.equals(neighbor.add(face.getVector())))
				{
					correctFace = face;
					break;
				}
			}

			Vec3d faceCenter = Vec3d.ofCenter(neighbor).add(Vec3d.of(correctFace.getVector()).multiply(0.5));

			BlockHitResult hit = MC.world.raycastBlock(RotationUtils.getEyesPos(), faceCenter, neighbor, BlockUtils.getBlockState(neighbor).getOutlineShape(MC.world, neighbor), BlockUtils.getBlockState(neighbor));
			if (hit == null)
			{
				neighborToClick = neighbor;
				directionToClick = correctFace;
				faceCenterToClick = faceCenter;
				break;
			}
		}

		// if no viable neighbor found
		if (neighborToClick == null)
			return false;

		CWHACK.getRotationFaker().setServerLookPos(faceCenterToClick);

		boolean sneak = MC.player.isSneaking();

		MC.player.setSneaking(true);
		ActionResult result = MC.interactionManager.interactBlock(MC.player, MC.world, Hand.MAIN_HAND, new BlockHitResult(faceCenterToClick, directionToClick, neighborToClick, false));
		MC.player.setSneaking(sneak);

		if (result == ActionResult.SUCCESS)
		{
			MC.player.swingHand(Hand.MAIN_HAND);
			return true;
		}

		return false;
	}

	public static void tryAirPlaceBlock(BlockPos pos)
	{
		if (BlockUtils.hasBlock(pos))
			return;

		Vec3d faceCenterToClick = Vec3d.ofBottomCenter(pos).add(0, 1, 0);

		CWHACK.getRotationFaker().setServerLookPos(faceCenterToClick);

		boolean sneak = MC.player.isSneaking();

		MC.player.setSneaking(true);
		ActionResult result = MC.interactionManager.interactBlock(MC.player, MC.world, Hand.MAIN_HAND, new BlockHitResult(faceCenterToClick, Direction.UP, pos, false));
		MC.player.setSneaking(sneak);

		if (result == ActionResult.SUCCESS)
			MC.player.swingHand(Hand.MAIN_HAND);
	}
}
