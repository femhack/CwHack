package net.cwhack.mixin;

import net.cwhack.events.AttackEntityListener.AttackEntityEvent;
import net.cwhack.events.StopUsingItemListener.StopUsingItemEvent;
import net.cwhack.mixinterface.IClientPlayerInteractionManager;
import net.cwhack.utils.MixinUtils;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin implements IClientPlayerInteractionManager
{

	@Override
	public void cwSyncSelectedSlot()
	{
		syncSelectedSlot();
	}

	@Shadow
	private boolean breakingBlock;

	@Override
	public void setBreakingBlock(boolean breakingBlock)
	{
		this.breakingBlock = breakingBlock;
	}

	@Shadow
	private BlockPos currentBreakingPos;

	@Override
	public void setCurrentBreakingPos(BlockPos pos)
	{
		currentBreakingPos = pos;
	}

	@Shadow
	private void syncSelectedSlot()
	{

	}

	@Inject(method = "stopUsingItem(Lnet/minecraft/entity/player/PlayerEntity;)V", at = @At("HEAD"), cancellable = true)
	private void onStopUsingItem(CallbackInfo ci)
	{
		MixinUtils.fireCancellableEvent(new StopUsingItemEvent(), ci);
	}

	@Inject(method = "attackEntity(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
	private void onAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci)
	{
		MixinUtils.fireCancellableEvent(new AttackEntityEvent(player, target), ci);
	}
}
