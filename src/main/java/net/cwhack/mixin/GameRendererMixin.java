package net.cwhack.mixin;

import net.cwhack.event.EventManager;
import net.cwhack.events.RenderListener.RenderEvent;
import net.cwhack.utils.NameTagUtils;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.math.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements AutoCloseable, SynchronousResourceReloader
{
	@Inject(
			at = {@At(value = "FIELD",
					target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z",
					opcode = Opcodes.GETFIELD,
					ordinal = 0)},
			method = {
					"renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V"},
			locals = LocalCapture.CAPTURE_FAILSOFT)
	private void onRenderWorld(float tickDelta, long finishTimeNano, MatrixStack matrices, CallbackInfo ci, boolean bl, Camera camera, MatrixStack matrixStack, double d, Matrix4f matrix4f)
	{
		RenderEvent event = new RenderEvent(matrices, tickDelta);
		EventManager.fire(event);
		NameTagUtils.onRender(matrices, matrix4f);
	}
}
