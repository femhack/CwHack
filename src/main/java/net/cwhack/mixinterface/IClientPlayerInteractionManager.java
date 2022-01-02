package net.cwhack.mixinterface;

import net.minecraft.util.math.BlockPos;

public interface IClientPlayerInteractionManager
{
	void cwSyncSelectedSlot();
	void setBreakingBlock(boolean breakingBlock);
	void setCurrentBreakingPos(BlockPos pos);
}
