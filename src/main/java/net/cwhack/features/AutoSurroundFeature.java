package net.cwhack.features;

import net.cwhack.BlockPlacer;
import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;
import net.cwhack.utils.BlockUtils;
import net.cwhack.utils.ChatUtils;
import net.cwhack.utils.InventoryUtils;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

import static net.cwhack.CwHack.MC;

public class AutoSurroundFeature extends Feature implements UpdateListener
{
	private final ArrayList<BlockPos> surround = new ArrayList<>();

	public AutoSurroundFeature()
	{
		super("AutoSurround", "Automatically surrounds you in holes");
		surround.add(new BlockPos(1, 0, 0));
		surround.add(new BlockPos(0, 0, 1));
		surround.add(new BlockPos(-1, 0, 0));
		surround.add(new BlockPos(0, 0, -1));
	}

	@Override
	protected void onEnable()
	{
		eventManager.add(UpdateListener.class, this, 1100);
	}

	@Override
	protected void onDisable()
	{
		eventManager.remove(UpdateListener.class, this);
	}

	@Override
	public void onUpdate()
	{
		if (MC.player.isUsingItem() || MC.player.isDead())
			return;

		int has = (int) surround.stream().filter(e -> BlockUtils.hasBlock(MC.player.getBlockPos().add(e))).count();
		if (has != 3)
			return;
		final BlockPos[] missing = new BlockPos[1];
		surround.forEach(e ->
		{
			if (!BlockUtils.hasBlock(MC.player.getBlockPos().add(e)))
				missing[0] = MC.player.getBlockPos().add(e);
		});

		int slot = MC.player.getInventory().selectedSlot;
		if (!InventoryUtils.selectItemFromHotbar(item -> item == Items.OBSIDIAN))
			ChatUtils.error("Do not have obsidian");

		if (!BlockPlacer.placeBlock(missing[0]))
			BlockPlacer.tryAirPlaceBlock(missing[0]);

		MC.player.getInventory().selectedSlot = slot;
	}
}
