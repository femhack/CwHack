package net.cwhack.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.cwhack.event.EventManager;
import net.cwhack.events.GUIRenderListener.GUIRenderEvent;
import net.cwhack.mixinterface.IInGameHud;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.cwhack.CwHack.MC;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper implements IInGameHud
{
	@Shadow
	protected abstract void renderHealthBar(MatrixStack matrices, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking);

//	@Inject(
//			at = {@At(value = "INVOKE",
//					target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableBlend()V",
//					ordinal = 4)},
//			method = {"render(Lnet/minecraft/client/util/math/MatrixStack;F)V"})
//	private void onRender(MatrixStack matrixStack, float partialTicks,
//	                      CallbackInfo ci)
//	{
//		if(CwHack.MC.options.debugEnabled)
//			return;
//
//		GUIRenderEvent event = new GUIRenderEvent(matrixStack, partialTicks);
//		EventManager.fire(event);
//	}

	@Inject(method = "render", at = @At("TAIL"))
	private void onRender(MatrixStack matrixStack, float partialTicks, CallbackInfo ci)
	{
		RenderSystem.setProjectionMatrix(Matrix4f.projectionMatrix(0, MC.getWindow().getFramebufferWidth(), 0, MC.getWindow().getFramebufferHeight(), 1000, 3000));

		GUIRenderEvent event = new GUIRenderEvent(matrixStack, partialTicks);
		EventManager.fire(event);

		RenderSystem.setProjectionMatrix(Matrix4f.projectionMatrix(0, (float) (MC.getWindow().getFramebufferWidth() / MC.getWindow().getScaleFactor()), 0, (float) (MC.getWindow().getFramebufferHeight() / MC.getWindow().getScaleFactor()), 1000, 3000));
		RenderSystem.applyModelViewMatrix();
	}

	@Override
	public void cwRenderHealthBar(MatrixStack matrices, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking)
	{
		renderHealthBar(matrices, player, x, y, lines, regeneratingHeartIndex, maxHealth, lastHealth, health, absorption, blinking);
	}
}