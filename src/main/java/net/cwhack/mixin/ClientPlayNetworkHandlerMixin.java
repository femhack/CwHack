package net.cwhack.mixin;

import net.cwhack.event.EventManager;
import net.cwhack.events.GameJoinListener.GameJoinEvent;
import net.cwhack.events.GameLeaveListener.GameLeaveEvent;
import net.cwhack.events.PacketOutputListener.PacketOutputEvent;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.cwhack.CwHack.CWHACK;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin
{
	@Shadow
	private ClientWorld world;
	private boolean worldNotNull;

	@Inject(at = @At("HEAD"), method = "onGameJoin")
	private void onGameJoinHead(GameJoinS2CPacket packet, CallbackInfo info) {
		worldNotNull = world != null;
	}

	@Inject(at = @At("TAIL"), method = "onGameJoin")
	private void onGameJoinTail(GameJoinS2CPacket packet, CallbackInfo info)
	{
		if (worldNotNull)
		{
			EventManager.fire(new GameLeaveEvent());
		}
		EventManager.fire(new GameJoinEvent());
	}

	@Inject(at = {@At("HEAD")},
			method = {"sendPacket(Lnet/minecraft/network/Packet;)V"},
			cancellable = true)
	private void onSendPacket(Packet<?> packet, CallbackInfo ci)
	{
		PacketOutputEvent event = new PacketOutputEvent(packet);
		EventManager.fire(event);

		if(event.isCancelled())
			ci.cancel();
	}

	@Redirect(at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/network/ClientPlayerEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"),
			method = {"onExplosion(Lnet/minecraft/network/packet/s2c/play/ExplosionS2CPacket;)V"})
	private void cancelExplosionKb(ClientPlayerEntity player, Vec3d velocity)
	{
		if (!CWHACK.getFeatures().velocityFeature.isEnabled())
			player.setVelocity(velocity);
	}

	@Inject(at = @At("HEAD"), method = "onPlaySound(Lnet/minecraft/network/packet/s2c/play/PlaySoundS2CPacket;)V")
	private void onPlaySound(PlaySoundS2CPacket packet, CallbackInfo ci)
	{

	}

	@Inject(at = @At("HEAD"), method = "onPlaySoundFromEntity(Lnet/minecraft/network/packet/s2c/play/PlaySoundFromEntityS2CPacket;)V")
	private void onPlaySoundFromEntity(PlaySoundFromEntityS2CPacket packet, CallbackInfo ci)
	{

	}

	@Inject(at = @At("HEAD"), method = "onPlaySoundId(Lnet/minecraft/network/packet/s2c/play/PlaySoundIdS2CPacket;)V")
	private void onPlaySoundId(PlaySoundIdS2CPacket packet, CallbackInfo ci)
	{

	}

	@ModifyArg(method = "onExplosion(Lnet/minecraft/network/packet/s2c/play/ExplosionS2CPacket;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/explosion/Explosion;affectWorld(Z)V"), index = 0)
	private boolean onExplosion(boolean particles)
	{
		return false;
	}
}