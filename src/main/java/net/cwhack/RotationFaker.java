package net.cwhack;

import net.cwhack.events.PostMotionListener;
import net.cwhack.events.PreMotionListener;
import net.cwhack.utils.RotationUtils;
import net.cwhack.utils.RotationUtils.Rotation;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class RotationFaker implements PreMotionListener, PostMotionListener
{

	private float realYaw;
	private float realPitch;
	private float serverYaw;
	private float serverPitch;
	private boolean faking = false;

	public RotationFaker()
	{
		CwHack.CWHACK.getEventManager().add(PreMotionListener.class, this);
		CwHack.CWHACK.getEventManager().add(PostMotionListener.class, this, Integer.MAX_VALUE);
	}

	public boolean isFaking()
	{
		return faking;
	}

	@Override
	public void onPreMotion()
	{
		if (!faking)
			return;
		ClientPlayerEntity player = CwHack.MC.player;
		realYaw = player.getYaw();
		realPitch = player.getPitch();
		player.setYaw(serverYaw);
		player.setPitch(serverPitch);
	}

	@Override
	public void onPostMotion()
	{
		if (!faking)
			return;
		ClientPlayerEntity player = CwHack.MC.player;
		player.setYaw(realYaw);
		player.setPitch(realPitch);
		faking = false;
	}

	public void setServerLookPos(Vec3d pos)
	{
		Rotation neededRotation = RotationUtils.getNeededRotations(pos);
		setServerLookAngle(neededRotation.getYaw(), neededRotation.getPitch());
	}

	public void setServerLookAngle(float yaw, float pitch)
	{
		serverYaw = yaw;
		serverPitch = pitch;
		faking = true;
	}

	public void setClientLookPos(Vec3d pos)
	{
		Rotation neededRotation = RotationUtils.getNeededRotations(pos);
		setClientLookAngle(neededRotation.getYaw(), neededRotation.getPitch());
	}

	public void setClientLookAngle(float yaw, float pitch)
	{
		CwHack.MC.player.setYaw(yaw);
		CwHack.MC.player.setPitch(pitch);
	}

	public float getServerYaw()
	{
		if (faking)
			return serverYaw;
		return CwHack.MC.player.getYaw();
	}

	public float getServerPitch()
	{
		if (faking)
			return serverPitch;
		return CwHack.MC.player.getPitch();
	}
}
