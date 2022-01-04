package net.cwhack.utils;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

public class HoleUtils
{
	;
	private static BlockPos[] surroundings = new BlockPos[]
			{
					new BlockPos(1, 0, 0),
					new BlockPos(0, 0, 1),
					new BlockPos(-1, 0, 0),
					new BlockPos(0, 0, -1)
			};

	public static boolean isFullySurrounded(BlockPos pos)
	{
		for (BlockPos surrounding : surroundings)
		{
			if (!BlockUtils.isBlock(Blocks.BEDROCK, pos.add(surrounding)))
				return false;
		}
		return true;
	}

	public static boolean isSurrounded(BlockPos pos)
	{
		for (BlockPos surrounding : surroundings)
		{
			if (!isSurroundingBlock(pos.add(surrounding)))
				return false;
		}
		return true;
	}

	private static boolean isSurroundingBlock(BlockPos pos)
	{
		return BlockUtils.isBlock(Blocks.BEDROCK, pos) || BlockUtils.isBlock(Blocks.OBSIDIAN, pos);
	}

	public static boolean wouldMakeAHole(BlockPos pos, BlockPos block)
	{
		for (BlockPos surrounding : surroundings)
		{
			BlockPos a = pos.add(surrounding);
			if (!isSurroundingBlock(a) && !a.equals(block))
				return false;
		}
		return true;
	}
}
