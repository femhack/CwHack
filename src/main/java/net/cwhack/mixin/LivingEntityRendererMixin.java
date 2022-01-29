package net.cwhack.mixin;

import net.cwhack.RotationFaker;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.cwhack.CwHack.CWHACK;
import static net.cwhack.CwHack.MC;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>>
{
	private float origYaw;
	private float origPitch;
	private float origPrevYaw;
	private float origPrevPitch;
	private boolean wasLastTimeFaked = false;

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void renderHead(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
		RotationFaker rotationFaker = CWHACK.getRotationFaker();
		if (livingEntity == MC.player && rotationFaker.wasFakingLastTick())
		{
			origPitch = MC.player.getPitch();
			origYaw = MC.player.headYaw;
			origPrevYaw = MC.player.prevYaw;
			origPrevPitch = MC.player.prevPitch;
			MC.player.headYaw = rotationFaker.getFakedYaw();
			MC.player.setPitch(rotationFaker.getFakedPitch());
			if (wasLastTimeFaked)
			{
				MC.player.prevHeadYaw = rotationFaker.getLastFakedYaw();
				MC.player.prevPitch = rotationFaker.getLastFakedPitch();
			}
			wasLastTimeFaked = true;
		}
		else
		{
			if (wasLastTimeFaked)
			{
				MC.player.prevHeadYaw = rotationFaker.getLastFakedYaw();
				MC.player.prevPitch = rotationFaker.getLastFakedPitch();
			}
			wasLastTimeFaked = false;
		}

		if (CWHACK.getFeatures().chamsFeature.isEnabled()) {
			GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
			GL11.glPolygonOffset(1.0f, -1100000.0f);
		}
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void renderTail(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
		if (livingEntity == MC.player && CWHACK.getRotationFaker().wasFakingLastTick())
		{
			MC.player.headYaw = origYaw;
			MC.player.setPitch(origPitch);
			MC.player.prevHeadYaw = origPrevYaw;
			MC.player.prevPitch = origPrevPitch;
		}

		if (CWHACK.getFeatures().chamsFeature.isEnabled()) {
			GL11.glPolygonOffset(1.0f, 1100000.0f);
			GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
		}
	}
}
