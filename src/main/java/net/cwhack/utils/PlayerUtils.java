package net.cwhack.utils;

import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.RaycastContext;

import static net.cwhack.CwHack.MC;

public enum PlayerUtils
{
	;

	public static void centerPlayer() {
		double x = MathHelper.floor(MC.player.getX()) + 0.5;
		double z = MathHelper.floor(MC.player.getZ()) + 0.5;
		MC.player.setPosition(x, MC.player.getY(), z);
		MC.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(MC.player.getX(), MC.player.getY(), MC.player.getZ(), MC.player.isOnGround()));
	}

	public static boolean canSeeEntity(Entity entity) {
		Vec3d vec1 = new Vec3d(MC.player.getX(), MC.player.getY() + MC.player.getStandingEyeHeight(), MC.player.getZ());
		Vec3d vec2 = new Vec3d(entity.getX(), entity.getY() + entity.getStandingEyeHeight(), entity.getZ());

		boolean canSeeFeet = MC.world.raycast(new RaycastContext(vec1, vec2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, MC.player)).getType() == HitResult.Type.MISS;

		boolean canSeeEyes = MC.world.raycast(new RaycastContext(vec1, vec2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, MC.player)).getType() == HitResult.Type.MISS;

		return canSeeFeet || canSeeEyes;
	}

	public static boolean isMoving() {
		return MC.player.forwardSpeed != 0 || MC.player.sidewaysSpeed != 0;
	}

	public static boolean isSprinting() {
		return MC.player.isSprinting() && (MC.player.forwardSpeed != 0 || MC.player.sidewaysSpeed != 0);
	}

	public static double distanceTo(Entity entity) {
		return distanceTo(entity.getX(), entity.getY(), entity.getZ());
	}

	public static double distanceTo(BlockPos blockPos) {
		return distanceTo(blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}

	public static double distanceTo(Vec3d vec3d) {
		return distanceTo(vec3d.getX(), vec3d.getY(), vec3d.getZ());
	}

	public static double distanceTo(double x, double y, double z) {
		float f = (float) (MC.player.getX() - x);
		float g = (float) (MC.player.getY() - y);
		float h = (float) (MC.player.getZ() - z);
		return MathHelper.sqrt(f * f + g * g + h * h);
	}

	public static double distanceToCamera(double x, double y, double z) {
		Camera camera = MC.gameRenderer.getCamera();
		return Math.sqrt(camera.getPos().squaredDistanceTo(x, y, z));
	}

	public static double distanceToCamera(Entity entity) {
		return distanceToCamera(entity.getX(), entity.getY(), entity.getZ());
	}

	public static GameMode getGameMode() {
		PlayerListEntry playerListEntry = MC.getNetworkHandler().getPlayerListEntry(MC.player.getUuid());
		if (playerListEntry == null) return GameMode.SPECTATOR;
		return playerListEntry.getGameMode();
	}

	public static GameMode getGameMode(PlayerEntity player)
	{
		PlayerListEntry playerListEntry = MC.getNetworkHandler().getPlayerListEntry(player.getUuid());
		if (playerListEntry == null) return GameMode.SPECTATOR;
		return playerListEntry.getGameMode();
	}

	public static double getTotalHealth() {
		return MC.player.getHealth() + MC.player.getAbsorptionAmount();
	}

	public static boolean isAlive() {
		return MC.player.isAlive() && !MC.player.isDead();
	}

	public static int getPing() {
		if (MC.getNetworkHandler() == null) return 0;

		PlayerListEntry playerListEntry = MC.getNetworkHandler().getPlayerListEntry(MC.player.getUuid());
		if (playerListEntry == null) return 0;
		return playerListEntry.getLatency();
	}
}
