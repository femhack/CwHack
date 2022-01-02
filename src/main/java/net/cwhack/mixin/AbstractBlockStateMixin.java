package net.cwhack.mixin;

import net.cwhack.event.EventManager;
import net.cwhack.events.IsFullCubeListener.IsFullCubeEvent;
import net.minecraft.block.AbstractBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin
{
	@Inject(at = {@At("TAIL")},
			method = {
					"isFullCube(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"},
			cancellable = true)
	private void onIsFullCube(BlockView world, BlockPos pos,
	                          CallbackInfoReturnable<Boolean> cir)
	{
		IsFullCubeEvent event = new IsFullCubeEvent();
		EventManager.fire(event);

		cir.setReturnValue(cir.getReturnValue() && !event.isCancelled());
	}
}