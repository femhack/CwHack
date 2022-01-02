package net.cwhack.features;

import net.cwhack.events.PacketInputListener;
import net.cwhack.feature.Feature;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

import static net.cwhack.CwHack.MC;

public class VelocityFeature extends Feature implements PacketInputListener
{
	public VelocityFeature()
	{
		super("Velocity", "prevent you from getting knockback");
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
		if (MC.world == null)
			return;

		if (!(event.getPacket() instanceof EntityVelocityUpdateS2CPacket))
			return;

		EntityVelocityUpdateS2CPacket packet = (EntityVelocityUpdateS2CPacket) event.getPacket();
		if (MC.world.getEntityById(packet.getId()) == MC.player)
			event.cancel();
	}
}
