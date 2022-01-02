package net.cwhack.features;

import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;
import net.cwhack.utils.InventoryUtils;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

import static net.cwhack.CwHack.MC;

public class AutoPearlFeature extends Feature implements UpdateListener
{

	public AutoPearlFeature()
	{
		super("AutoPearl", "automatically throws a pearl");
	}

	@Override
	protected void onEnable()
	{
		eventManager.add(UpdateListener.class, this);
	}

	@Override
	protected void onDisable()
	{
		eventManager.remove(UpdateListener.class, this);
	}

	@Override
	public void onUpdate()
	{
		int i = MC.player.getInventory().selectedSlot;
		if (InventoryUtils.selectItemFromHotbar(Items.ENDER_PEARL))
		{
			MC.player.swingHand(Hand.MAIN_HAND);
			MC.interactionManager.interactItem(MC.player, MC.world, Hand.MAIN_HAND);
		}
		MC.player.getInventory().selectedSlot = i;
		setEnabled(false);
	}
}
