package net.cwhack.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.cwhack.CwHack.CWHACK;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity>
{
	@Inject(at = {@At("HEAD")},
			method = {
					"renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"},
			cancellable = true)
	private void onRenderLabelIfPresent(T entity, Text text,
	                                    MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider,
	                                    int i, CallbackInfo ci)
	{
		if (CWHACK.getFeatures().nameTagFeature.isEnabled())
			ci.cancel();
	}
}
