package net.cwhack.features;

import net.cwhack.CwHack;
import net.cwhack.events.PacketInputListener;
import net.cwhack.feature.Feature;
import net.cwhack.setting.TextSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

public class AutoEzFeature extends Feature implements PacketInputListener
{
	private final TextSetting ezpop = new TextSetting("ezpopText", "This will be sent in chat when someone pops a totem", "ez pop", this);
	private final TextSetting ggez = new TextSetting("ggezText", "This will be sent in chat when someone dies", "gg ez no re", this);

	public AutoEzFeature()
	{
		super("AutoEz", "Automatically put a specific line of text in chat after someone pops or dies");
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

		Entity entity = packet.getEntity(CwHack.MC.world);
		if (!(entity instanceof PlayerEntity))
			return;
		if (entity == CwHack.MC.player)
			return;
		PlayerEntity player = (PlayerEntity) entity;

		if (packet.getStatus() == 35)
		{
			CwHack.MC.player.sendChatMessage(ezpop.getValue() + " " + player.getEntityName());
		}
		if (packet.getStatus() == 3)
		{
			CwHack.MC.player.sendChatMessage(ggez.getValue());
		}
	}
}
