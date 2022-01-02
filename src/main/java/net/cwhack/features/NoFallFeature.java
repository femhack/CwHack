package net.cwhack.features;

import net.cwhack.events.PostMotionListener;
import net.cwhack.events.PreMotionListener;
import net.cwhack.events.SendMovementPacketsListener;
import net.cwhack.feature.Feature;
import net.minecraft.entity.player.PlayerEntity;

import static net.cwhack.CwHack.MC;

public class NoFallFeature extends Feature implements SendMovementPacketsListener, PreMotionListener, PostMotionListener
{

	public NoFallFeature()
	{
		super("NoFall", "cancel fall damage");
	}

	@Override
	protected void onEnable()
	{
		eventManager.add(SendMovementPacketsListener.class, this);
		eventManager.add(PreMotionListener.class, this);
		eventManager.add(PostMotionListener.class, this);
	}

	@Override
	protected void onDisable()
	{
		eventManager.remove(SendMovementPacketsListener.class, this);
		eventManager.remove(PreMotionListener.class, this);
		eventManager.remove(PostMotionListener.class, this);
	}

	private boolean origOnGround = false;

	@Override
	public void onSendMovementPackets(SendMovementPacketsEvent event)
	{
		origOnGround = MC.player.isOnGround();
	}

	@Override
	public void onPreMotion()
	{
		PlayerEntity player = MC.player;
		if (player.fallDistance <= (player.isFallFlying() ? 1 : 2))
			return;
		if (player.isFallFlying() && player.getVelocity().y > -0.5)
			return;
		player.setOnGround(true);
	}

	@Override
	public void onPostMotion()
	{
		MC.player.setOnGround(origOnGround);
	}
}
