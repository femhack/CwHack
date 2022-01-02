package net.cwhack.features;

import net.cwhack.CwHack;
import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import java.util.Random;

public class DerpFeature extends Feature implements UpdateListener
{

	private Random random;

	public DerpFeature()
	{
		super("Derp", "Automatically moves your head around randomly");
		random = new Random();
	}

	@Override
	public void onEnable()
	{
		eventManager.add(UpdateListener.class, this);
	}

	@Override
	public void onDisable()
	{
		eventManager.remove(UpdateListener.class, this);
	}

	@Override
	public void onUpdate()
	{
		float yaw = CwHack.MC.player.getYaw() + random.nextFloat() * 180.0f - 90.0f;
		float pitch = random.nextFloat() * 180.0f - 90.0f;
		CwHack.MC.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, CwHack.MC.player.isOnGround()));
	}
}
