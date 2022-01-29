package net.cwhack.mixin;

import net.cwhack.event.EventManager;
import net.cwhack.events.GetOutlineShapeListener.GetOutlineShapeEvent;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin
{
	@Inject(at = {@At("HEAD")}, method = {"getOutlineShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;"}, cancellable = true)
	private void onGetOutlineShape(BlockView view, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir)
	{
		GetOutlineShapeEvent event = new GetOutlineShapeEvent(view, pos, context);
		EventManager.fire(event);
		if (event.getReturnValue() != null)
			cir.setReturnValue(event.getReturnValue());
	}
}