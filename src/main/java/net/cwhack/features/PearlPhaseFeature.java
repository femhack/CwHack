package net.cwhack.features;

import net.cwhack.events.PlayerJumpListener;
import net.cwhack.events.PlayerTickMovementListener;
import net.cwhack.events.SetOpaqueCubeListener;
import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import static net.cwhack.CwHack.MC;

public class PearlPhaseFeature extends Feature implements UpdateListener, PlayerTickMovementListener, SetOpaqueCubeListener, PlayerJumpListener
{

	public PearlPhaseFeature()
	{
		super("PearlPhase", "allow you to phase through blocks using ender pearls");
	}

	@Override
	protected void onEnable()
	{
		eventManager.add(UpdateListener.class, this);
		eventManager.add(PlayerTickMovementListener.class, this);
		eventManager.add(SetOpaqueCubeListener.class, this);
		eventManager.add(PlayerJumpListener.class, this);
	}

	@Override
	protected void onDisable()
	{
		eventManager.remove(UpdateListener.class, this);
		eventManager.remove(PlayerTickMovementListener.class, this);
		eventManager.remove(SetOpaqueCubeListener.class, this);
		eventManager.remove(PlayerJumpListener.class, this);
		MC.player.noClip = false;
	}

	@Override
	public void onPlayerTickMovement(PlayerTickMovementEvent event)
	{
		if (!collidingBlocks())
			return;
		ClientPlayerEntity player = MC.player;
		player.noClip = true;
	}

	@Override
	public void onSetOpaqueCube(SetOpaqueCubeEvent event)
	{
		event.cancel();
	}

	@Override
	public void onUpdate()
	{
		if (!collidingBlocks())
			return;

		ClientPlayerEntity player = MC.player;

		player.noClip = true;
		player.fallDistance = 0;
		player.setOnGround(true);

		player.getAbilities().flying = false;
		player.setVelocity(0, 0, 0);

		float speed = 0.02F;
		player.airStrafingSpeed = speed;

		if (MC.options.keyJump.isPressed())
		{
			player.addVelocity(0, speed, 0);
		}
		if (MC.options.keySneak.isPressed())
		{
			player.addVelocity(0, -speed, 0);
		}
	}

	@Override
	public void onPlayerJump(PlayerJumpEvent event)
	{
		if (!collidingBlocks())
			return;
		event.cancel();
	}

	private boolean collidingBlocks()
	{
		ClientPlayerEntity player = MC.player;
		return
			wouldCollideAt(new BlockPos(player.getX() - (double)player.getWidth() * 0.35D, player.getY(), player.getZ() + (double)player.getWidth() * 0.35D)) ||
			wouldCollideAt(new BlockPos(player.getX() - (double)player.getWidth() * 0.35D, player.getY(), player.getZ() - (double)player.getWidth() * 0.35D)) ||
			wouldCollideAt(new BlockPos(player.getX() + (double)player.getWidth() * 0.35D, player.getY(),player.getZ() - (double)player.getWidth() * 0.35D)) ||
			wouldCollideAt(new BlockPos(player.getX() + (double)player.getWidth() * 0.35D, player.getY(),player.getZ() + (double)player.getWidth() * 0.35D));
	}

	private boolean wouldCollideAt(BlockPos pos)
	{
		Box box = MC.player.getBoundingBox();
		Box box2 = (new Box(pos.getX(), box.minY, pos.getZ(), (double)pos.getX() + 1.0D, box.maxY, (double)pos.getZ() + 1.0D)).contract(1.0E-7D);
		return MC.world.canCollide(MC.player, box2);
	}
}
