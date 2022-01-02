package net.cwhack.features;

import net.cwhack.events.PacketInputListener;
import net.cwhack.events.PlayerTickMovementListener;
import net.cwhack.events.SendMovementPacketsListener;
import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;
import net.cwhack.utils.RotationUtils;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

import static net.cwhack.CwHack.MC;

public class PacketFlyFeature extends Feature implements UpdateListener, SendMovementPacketsListener, PlayerTickMovementListener, PacketInputListener
{

	public PacketFlyFeature()
	{
		super("PacketFly", "ree go brr brr");
	}

	@Override
	protected void onEnable()
	{
		eventManager.add(UpdateListener.class, this);
		eventManager.add(SendMovementPacketsListener.class, this);
		eventManager.add(PlayerTickMovementListener.class, this);
		eventManager.add(PacketInputListener.class, this, 1);
	}

	@Override
	protected void onDisable()
	{
		eventManager.remove(UpdateListener.class, this);
		eventManager.remove(SendMovementPacketsListener.class, this);
		eventManager.remove(PlayerTickMovementListener.class, this);
		eventManager.remove(PacketInputListener.class, this);
	}

	@Override
	public void onUpdate()
	{
		MC.player.setVelocity(0, 0, 0);

		double speedY = 0;

		if (MC.player.age % 4 == 0)
			speedY -= 0.04f;

		Vec3d dir = Vec3d.ZERO;

		if (MC.options.keyForward.isPressed())
			dir = RotationUtils.getClientLookVec().multiply(1, 0, 1).normalize().multiply(0.031);

		dir = dir.add(0, speedY, 0);

		MC.player.setVelocity(dir);

		double x = MC.player.getX() + dir.x;
		double y = MC.player.getY() + dir.y;
		double z = MC.player.getZ() + dir.z;

		MC.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(x, y, z, MC.player.getYaw(), MC.player.getPitch(), MC.player.isOnGround()));

//		x += new Random().nextInt(100000);
//		z += new Random().nextInt(100000);

		y -= 1337;

		MC.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, MC.player.isOnGround()));
	}

	@Override
	public void onSendMovementPackets(SendMovementPacketsEvent event)
	{
		event.cancel();
	}

	@Override
	public void onPlayerTickMovement(PlayerTickMovementEvent event)
	{
	}

	@Override
	public void onReceivePacket(PacketInputEvent event)
	{
	}
}
