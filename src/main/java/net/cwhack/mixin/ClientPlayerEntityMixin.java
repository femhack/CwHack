package net.cwhack.mixin;

import com.mojang.authlib.GameProfile;
import net.cwhack.event.EventManager;
import net.cwhack.events.ChatOutputListener.ChatOutputEvent;
import net.cwhack.events.IsPlayerInLavaListener.IsPlayerInLavaEvent;
import net.cwhack.events.IsPlayerTouchingWaterListener.IsPlayerTouchingWaterEvent;
import net.cwhack.events.PlayerJumpListener.PlayerJumpEvent;
import net.cwhack.events.PlayerMoveListener.PlayerMoveEvent;
import net.cwhack.events.PlayerTickMovementListener.PlayerTickMovementEvent;
import net.cwhack.events.PostMotionListener.PostMotionEvent;
import net.cwhack.events.PostUpdateListener.PostUpdateEvent;
import net.cwhack.events.PreMotionListener.PreMotionEvent;
import net.cwhack.events.SendMovementPacketsListener.SendMovementPacketsEvent;
import net.cwhack.events.UpdateListener.UpdateEvent;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.cwhack.CwHack.CWHACK;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity
{
	@Shadow
	private ClientPlayNetworkHandler networkHandler;

	public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile)
	{
		super(world, profile);
	}

	@Shadow
	protected abstract void autoJump(float dx, float dz);

	public void move(MovementType movementType, Vec3d movement)
	{
		PlayerMoveEvent event = new PlayerMoveEvent(movementType, movement);
		EventManager.fire(event);
		if (event.isCancelled())
			return;
		movementType = event.getMovementType();
		movement = event.getMovement();
		double d = this.getX();
		double e = this.getZ();
		super.move(movementType, movement);
		this.autoJump((float)(this.getX() - d), (float)(this.getZ() - e));
	}

	@Inject(at = {@At("HEAD")},
			method = {
					"tickMovement()V"},
			cancellable = true)
	private void onTickMovement(CallbackInfo ci)
	{
		PlayerTickMovementEvent event = new PlayerTickMovementEvent();
		EventManager.fire(event);
		if (event.isCancelled())
			ci.cancel();
	}

	@Override
	public void jump()
	{
		PlayerJumpEvent event = new PlayerJumpEvent();
		EventManager.fire(event);
		if (!event.isCancelled())
			super.jump();
	}

	@Override
	public boolean isTouchingWater()
	{
		IsPlayerTouchingWaterEvent event = new IsPlayerTouchingWaterEvent(super.isTouchingWater());
		EventManager.fire(event);
		return event.isTouchingWater();
	}

	@Override
	public boolean isInLava()
	{
		IsPlayerInLavaEvent event = new IsPlayerInLavaEvent(super.isInLava());
		EventManager.fire(event);
		return event.isInLava();
	}

	@Inject(at = @At("HEAD"),
			method = "sendChatMessage(Ljava/lang/String;)V",
			cancellable = true)
	private void onSendChatMessage(String message, CallbackInfo ci)
	{
		ChatOutputEvent event = new ChatOutputEvent(message);
		EventManager.fire(event);

		if(event.isCancelled())
		{
			ci.cancel();
			return;
		}

		if(!event.isModified())
			return;

		ChatMessageC2SPacket packet =
				new ChatMessageC2SPacket(event.getMessage());
		networkHandler.sendPacket(packet);
		ci.cancel();
	}

	@Inject(at = {@At("HEAD")}, method = {"sendMovementPackets()V"}, cancellable = true)
	private void onSendMovementPackets(CallbackInfo ci)
	{
		SendMovementPacketsEvent event = new SendMovementPacketsEvent();
		EventManager.fire(event);
		if (event.isCancelled())
			ci.cancel();
	}

	@Inject(at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isCamera()Z", shift = At.Shift.BEFORE)}, method = {"sendMovementPackets()V"})
	private void onSendMovementPacketsHEAD(CallbackInfo ci)
	{
		EventManager.fire(new PreMotionEvent());
	}

	@Inject(at = {@At("TAIL")}, method = {"sendMovementPackets()V"})
	private void onSendMovementPacketsTAIL(CallbackInfo ci)
	{
		EventManager.fire(new PostMotionEvent());
	}

	@Inject(at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V",
			ordinal = 0), method = "tick()V")
	private void onTick(CallbackInfo ci)
	{
		EventManager.fire(new UpdateEvent());
	}

	@Inject(at = @At(value = "HEAD"), method = "tick()V")
	private void onPostTick(CallbackInfo ci)
	{
		EventManager.fire(new PostUpdateEvent());
	}

	@Redirect(at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z",
			ordinal = 0), method = "tickMovement()V")
	private boolean isUsingItem(ClientPlayerEntity player)
	{
		if(CWHACK.getFeatures().noSlowFeature.isEnabled())
			return false;

		return player.isUsingItem();
	}
}
