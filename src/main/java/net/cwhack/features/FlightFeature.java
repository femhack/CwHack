package net.cwhack.features;

import net.cwhack.events.PostMotionListener;
import net.cwhack.events.PreMotionListener;
import net.cwhack.events.SendMovementPacketsListener;
import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;
import net.cwhack.setting.DecimalSetting;
import net.cwhack.setting.EnumSetting;
import net.cwhack.utils.BlockUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

import static net.cwhack.CwHack.MC;

public class FlightFeature extends Feature implements UpdateListener, SendMovementPacketsListener, PreMotionListener, PostMotionListener
{

	private final DecimalSetting speed = new DecimalSetting("speed", "the speed of flying", 0.5, this);
	private final EnumSetting<BypassSetting> bypass = new EnumSetting<>("bypass", "the bypass method this module will use", BypassSetting.values(), BypassSetting.NONE, this);

	enum BypassSetting
	{
		NONE,
		SPOOF_ONGROUND
	}

	public FlightFeature()
	{
		super("Flight", "reeee");
	}

	@Override
	protected void onEnable()
	{
		eventManager.add(UpdateListener.class, this);
		eventManager.add(SendMovementPacketsListener.class, this);
		eventManager.add(PreMotionListener.class, this);
		eventManager.add(PostMotionListener.class, this);
		lastModified = System.currentTimeMillis();
	}

	@Override
	protected void onDisable()
	{
		eventManager.remove(UpdateListener.class, this);
		eventManager.remove(SendMovementPacketsListener.class, this);
		eventManager.remove(PreMotionListener.class, this);
		eventManager.remove(PostMotionListener.class, this);
	}

	@Override
	public void onUpdate()
	{
		ClientPlayerEntity player = MC.player;

		player.airStrafingSpeed = speed.getValueF();

		player.setVelocity(0, 0, 0);
		Vec3d velocity = player.getVelocity();

		if(MC.options.keyJump.isPressed())
			player.setVelocity(velocity.add(0, speed.getValue(), 0));

		if(MC.options.keySneak.isPressed())
			player.setVelocity(velocity.subtract(0, speed.getValue(), 0));
	}

	private long lastModified = 0;
	private double origY = 0;
	private boolean origOnGround = false;

	@Override
	public void onSendMovementPackets(SendMovementPacketsEvent event)
	{
		origOnGround = MC.player.isOnGround();
	}

	@Override
	public void onPreMotion()
	{
		// vanilla anti-flying bypass
		long currentTime = System.currentTimeMillis();
		long sinceModified = currentTime - lastModified;
		Vec3d pos = MC.player.getPos();
		origY = pos.y;
		if (sinceModified > 1000
				&& !BlockUtils.hasBlock(MC.player.getBlockPos().down()))
		{
			MC.player.setPosition(pos.subtract(0, 0.0313, 0));
			lastModified = currentTime;
		}

		if (bypass.getValue() == BypassSetting.SPOOF_ONGROUND)
		{
			MC.player.setOnGround(true);
		}
	}

	@Override
	public void onPostMotion()
	{
		Vec3d pos = MC.player.getPos();
		MC.player.setPosition(pos.x, origY, pos.z);
		MC.player.setOnGround(origOnGround);
	}

}
