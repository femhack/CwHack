package net.cwhack.features;

import net.cwhack.CwHack;
import net.cwhack.events.PacketInputListener;
import net.cwhack.feature.Feature;
import net.cwhack.setting.TextSetting;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

public class AutoCopeFeature extends Feature implements PacketInputListener
{
	private TextSetting cope = new TextSetting("copeText", "This will be sent in chat when you die", "faggot kys");
	public AutoCopeFeature()
	{
		super("AutoCope", "Automatically sends an excuse in game chat when you die");
		addSetting(cope);
	}

	@Override
	protected void onEnable()
	{
		eventManager.add(PacketInputListener.class, this);
	}

	@Override
	protected void onDisable()
	{
		eventManager.remove(PacketInputListener.class, this);
	}

	@Override
	public void onReceivePacket(PacketInputEvent event)
	{
		if (!(event.getPacket() instanceof EntityStatusS2CPacket))
			return;

		EntityStatusS2CPacket packet = (EntityStatusS2CPacket) event.getPacket();

		if (CwHack.MC.world == null)
			return;

		if (packet.getEntity(CwHack.MC.world) != CwHack.MC.player)
			return;

		if (packet.getStatus() != 3)
			return;

		CwHack.MC.player.sendChatMessage(cope.getValue());
	}
}
