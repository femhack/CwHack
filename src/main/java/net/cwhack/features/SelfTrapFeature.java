package net.cwhack.features;

import net.cwhack.BlockPlacer;
import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;
import net.cwhack.utils.BlockUtils;
import net.cwhack.utils.ChatUtils;
import net.cwhack.utils.InventoryUtils;
import net.cwhack.utils.PlayerUtils;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

import static net.cwhack.CwHack.MC;

public class SelfTrapFeature extends Feature implements UpdateListener
{

	private final ArrayList<BlockPos> trap = new ArrayList<>();

	private int i;

	public SelfTrapFeature()
	{
		super("SelfTrap", "Automatically trap yourself in an obsidian box");
		trap.add(new BlockPos(1, 0, 0));
		trap.add(new BlockPos(0, 0, 1));
		trap.add(new BlockPos(-1, 0, 0));
		trap.add(new BlockPos(0, 0, -1));
		trap.add(new BlockPos(1, 1, 0));
		trap.add(new BlockPos(0, 1, 1));
		trap.add(new BlockPos(-1, 1, 0));
		trap.add(new BlockPos(0, 1, -1));
		trap.add(new BlockPos(0, 2, 0));
	}

	@Override
	public void onEnable()
	{
		eventManager.add(UpdateListener.class, this, 1000);
		i = 0;
	}

	@Override
	public void onDisable()
	{
		eventManager.remove(UpdateListener.class, this);
	}

	@Override
	public void onUpdate()
	{
		if (!MC.player.isOnGround())
		{
			setEnabled(false);
			return;
		}

		if (!InventoryUtils.selectItemFromHotbar(item -> item == Items.OBSIDIAN))
		{
			ChatUtils.error("Do not have obsidian");
			setEnabled(false);
			return;
		}

		PlayerUtils.centerPlayer();

		BlockPos pos;
		do
		{
			pos = MC.player.getBlockPos().add(trap.get(i));
			i++;
		} while (BlockUtils.hasBlock(pos) && i != trap.size());
		if (!BlockUtils.hasBlock(pos))
		{
			int slot = MC.player.getInventory().selectedSlot;
			if (!InventoryUtils.selectItemFromHotbar(item -> item == Items.OBSIDIAN))
				ChatUtils.error("Do not have obsidian");



			if (!BlockPlacer.placeBlock(pos))
				BlockPlacer.tryAirPlaceBlock(pos);

			MC.player.getInventory().selectedSlot = slot;
		}
		if (i == trap.size())
			setEnabled(false);
	}
}
