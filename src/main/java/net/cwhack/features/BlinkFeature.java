package net.cwhack.features;

import net.cwhack.CwHack;
import net.cwhack.events.PacketOutputListener;
import net.cwhack.feature.Feature;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import java.util.ArrayDeque;

public class BlinkFeature extends Feature implements PacketOutputListener
{

	private ArrayDeque<PlayerMoveC2SPacket> throttledPackets = new ArrayDeque<>();

	public BlinkFeature()
	{
		super("Blink", "De-sync server and client player position by throttling movement packets");
	}

	@Override
	protected void onEnable()
	{
		eventManager.add(PacketOutputListener.class, this);
	}

	@Override
	protected void onDisable()
	{
		eventManager.remove(PacketOutputListener.class, this);
		throttledPackets.forEach(p -> CwHack.MC.player.networkHandler.sendPacket(p));
		throttledPackets.clear();
	}

	@Override
	public void onSendPacket(PacketOutputEvent event)
	{
		if (!(event.getPacket() instanceof PlayerMoveC2SPacket))
			return;

		event.cancel();
		PlayerMoveC2SPacket packet = (PlayerMoveC2SPacket) event.getPacket();
		PlayerMoveC2SPacket lastPacket = throttledPackets.peekLast();

		if (lastPacket != null && packet.isOnGround() == lastPacket.isOnGround()
				&& packet.getYaw(-1) == lastPacket.getYaw(-1)
				&& packet.getPitch(-1) == lastPacket.getPitch(-1)
				&& packet.getX(-1) == lastPacket.getX(-1)
				&& packet.getY(-1) == lastPacket.getY(-1)
				&& packet.getZ(-1) == lastPacket.getZ(-1))
			return;

		throttledPackets.addLast(packet);
	}

	public void cancel()
	{
		throttledPackets.clear();
		setEnabled(false);
	}
}
