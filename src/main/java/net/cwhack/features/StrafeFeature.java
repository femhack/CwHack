package net.cwhack.features;

import net.cwhack.events.PlayerJumpListener;
import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;
import net.cwhack.setting.DecimalSetting;
import net.cwhack.setting.IntegerSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import static net.cwhack.CwHack.MC;

public class StrafeFeature extends Feature implements UpdateListener, PlayerJumpListener
{

	private final DecimalSetting ncpSpeed = new DecimalSetting("speed", "speed of strafing", 0.25);
	private final IntegerSetting disableAfter = new IntegerSetting("disableAfter", "disable after certain amount of time", 0);

	private int clock = 0;

	private double distance = 0;
	private double speed = 0;
	private int state = 0;

	public StrafeFeature()
	{
		super("Strafe", "strafe");
		addSetting(ncpSpeed);
		addSetting(disableAfter);
	}

	private void reset()
	{
		state = 0;
		distance = 0;
		speed = 0.2873;
	}

	@Override
	protected void onEnable()
	{
		clock = 0;
		reset();
		eventManager.add(UpdateListener.class, this);
		eventManager.add(PlayerJumpListener.class, this);
	}

	@Override
	protected void onDisable()
	{
		eventManager.remove(UpdateListener.class, this);
		eventManager.remove(PlayerJumpListener.class, this);
	}

	@Override
	public void onUpdate()
	{
		clock++;
		if (clock > disableAfter.getValue() && disableAfter.getValue() != 0)
		{
			clock = 0;
			setEnabled(false);
			return;
		}
		ClientPlayerEntity player = MC.player;

		if (MC.player.isTouchingWater())
			return;

		float f = 0.017453292F;
		float pi = (float)Math.PI;

		float f1 = MathHelper.cos(-player.getYaw() * f - pi);
		float f2 = MathHelper.sin(-player.getYaw() * f - pi);

		Vec3d dir = new Vec3d(-f2, 0, -f1);
		Vec3d up = new Vec3d(0, 1, 0);
		Vec3d left = up.crossProduct(dir);
		Vec3d right = dir.crossProduct(up);

		Vec3d pos = player.getPos();

		Vec3d delta = Vec3d.ZERO;

		if (MC.options.keyForward.isPressed())
		{
			delta = delta.add(dir);
		}
		if (MC.options.keyBack.isPressed())
		{
			delta = delta.subtract(dir);
		}
		if (MC.options.keyLeft.isPressed())
		{
			delta = delta.add(left);
		}
		if (MC.options.keyRight.isPressed())
		{
			delta = delta.add(right);
		}

		delta = delta.normalize().multiply(ncpSpeed.getValue());

		if (!player.isOnGround())
			MC.player.setVelocity(delta.x, player.getVelocity().y, delta.z);

		if (MC.options.keyJump.isPressed() && player.isOnGround())
		{
			MC.player.setVelocity(delta.x, 0.4, delta.z);
		}
	}

	@Override
	public void onPlayerJump(PlayerJumpEvent event)
	{
		event.cancel();
	}
}
