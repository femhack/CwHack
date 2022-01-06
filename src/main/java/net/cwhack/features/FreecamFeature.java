package net.cwhack.features;

import net.cwhack.events.*;
import net.cwhack.feature.Feature;
import net.cwhack.setting.DecimalSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec3d;

import static net.cwhack.CwHack.MC;

public class FreecamFeature extends Feature implements UpdateListener, PlayerTickMovementListener, PacketOutputListener, PacketInputListener, IsPlayerTouchingWaterListener, IsPlayerInLavaListener, SetOpaqueCubeListener, GameLeaveListener
{

	private final DecimalSetting speed = new DecimalSetting("speed", "speed", 1.0, this);

	private Vec3d oldPos;
	private float oldYaw;
	private float oldPitch;

	public FreecamFeature()
	{
		super("Freecam", "allow you to move freely at client side");
	}

	@Override
	protected void onEnable()
	{
		eventManager.add(UpdateListener.class, this);
		eventManager.add(PlayerTickMovementListener.class, this);
		eventManager.add(PacketOutputListener.class, this);
		eventManager.add(PacketInputListener.class, this);
		eventManager.add(IsPlayerTouchingWaterListener.class, this);
		eventManager.add(IsPlayerInLavaListener.class, this);
		eventManager.add(SetOpaqueCubeListener.class, this);
		eventManager.add(GameLeaveListener.class, this);
		if (MC.player == null)
			return;
		oldPos = MC.player.getPos();
		oldYaw = MC.player.getYaw();
		oldPitch = MC.player.getPitch();
	}

	@Override
	protected void onDisable()
	{
		eventManager.remove(UpdateListener.class, this);
		eventManager.remove(PlayerTickMovementListener.class, this);
		eventManager.remove(PacketOutputListener.class, this);
		eventManager.remove(PacketInputListener.class, this);
		eventManager.remove(IsPlayerTouchingWaterListener.class, this);
		eventManager.remove(IsPlayerInLavaListener.class, this);
		eventManager.remove(SetOpaqueCubeListener.class, this);
		eventManager.remove(GameLeaveListener.class, this);
		if (MC.player == null)
			return;
		MC.player.noClip = false;
		MC.player.setPosition(oldPos);
		MC.player.setYaw(oldYaw);
		MC.player.setPitch(oldPitch);
		MC.player.setVelocity(Vec3d.ZERO);
		MC.worldRenderer.reload();
	}

	@Override
	public void onUpdate()
	{
		ClientPlayerEntity player = MC.player;
		player.setVelocity(Vec3d.ZERO);

		player.setPose(EntityPose.STANDING);

		player.setOnGround(false);
		player.airStrafingSpeed = speed.getValueF();
		Vec3d velocity = player.getVelocity();

		if(MC.options.keyJump.isPressed())
			player.setVelocity(velocity.add(0, speed.getValue(), 0));

		if(MC.options.keySneak.isPressed())
			player.setVelocity(velocity.subtract(0, speed.getValue(), 0));
	}

	@Override
	public void onPlayerTickMovement(PlayerTickMovementEvent event)
	{
		MC.player.noClip = true;
	}

	@Override
	public void onSendPacket(PacketOutputEvent event)
	{
		if (event.getPacket() instanceof PlayerMoveC2SPacket packet)
			event.cancel();
	}

	@Override
	public void onReceivePacket(PacketInputEvent event)
	{
		if (event.getPacket() instanceof PlayerPositionLookS2CPacket packet)
		{
			MC.getNetworkHandler().sendPacket(new TeleportConfirmC2SPacket(packet.getTeleportId()));
			oldPos = new Vec3d(packet.getX(), packet.getY(), packet.getZ());
			oldYaw = packet.getYaw();
			oldPitch = packet.getPitch();
			event.cancel();
		}
	}

	@Override
	public void onIsPlayerTouchingWater(IsPlayerTouchingWaterEvent event)
	{
		event.setTouchingWater(false);
	}

	@Override
	public void onIsPlayerInLava(IsPlayerInLavaEvent event)
	{
		event.setInLava(false);
	}

	@Override
	public void onSetOpaqueCube(SetOpaqueCubeEvent event)
	{
		event.cancel();
	}

	@Override
	public void onGameLeave()
	{
		setEnabled(false);
	}
}
