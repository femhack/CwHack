package net.cwhack.features;

import net.cwhack.events.StopUsingItemListener;
import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;
import net.cwhack.utils.ChatUtils;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import static net.cwhack.CwHack.MC;

public class Bow32kFeature extends Feature implements StopUsingItemListener, UpdateListener
{
	// testing
	private boolean activated = false;
	private boolean letgo = false;

	public Bow32kFeature()
	{
		super("Bow32k", "one shot someone with bow");
	}

	@Override
	protected void onEnable()
	{
		eventManager.add(StopUsingItemListener.class, this);
		eventManager.add(UpdateListener.class, this);
	}

	@Override
	protected void onDisable()
	{
		eventManager.remove(StopUsingItemListener.class, this);
		eventManager.remove(UpdateListener.class, this);
	}

	@Override
	public void onStopUsingItem(StopUsingItemEvent event)
	{
		if (letgo)
			return;
		if (!MC.player.isHolding(Items.BOW))
			return;
		activated = true;
		ChatUtils.info("pu");

		MC.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(MC.player.getX(), MC.player.getY() + 1.0, MC.player.getZ(), MC.player.isOnGround()));
	}

	@Override
	public void onUpdate()
	{
		if (!activated)
			return;
		activated = false;
		letgo = true;
		MC.interactionManager.stopUsingItem(MC.player);
		letgo = false;
	}
}
