package net.cwhack.features;

import com.mojang.authlib.GameProfile;
import net.cwhack.feature.Feature;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import static net.cwhack.CwHack.MC;

public class FakePlayerFeature extends Feature
{

	public FakePlayerFeature()
	{
		super("FakePlayer", "spawn a fake player for testing purposes");
	}

	int id;

	@Override
	protected void onEnable()
	{
		OtherClientPlayerEntity player = new OtherClientPlayerEntity(MC.world, new GameProfile(null, "fit"));
		Vec3d pos = MC.player.getPos();
		player.updateTrackedPosition(pos);
		player.updatePositionAndAngles(pos.x, pos.y, pos.z, MC.player.getYaw(), MC.player.getPitch());
		player.resetPosition();
		MC.world.addPlayer(player.getId(), player);
		id = player.getId();
	}

	@Override
	protected void onDisable()
	{
		MC.world.removeEntity(id, Entity.RemovalReason.DISCARDED);
	}
}
