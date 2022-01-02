package net.cwhack.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.cwhack.CwHack.CWHACK;

@Mixin(Camera.class)
public class CameraMixin
{
	@Inject(at = {@At("HEAD")},
			method = {"clipToSpace(D)D"},
			cancellable = true)
	private void onClipToSpace(double desiredCameraDistance,
	                           CallbackInfoReturnable<Double> cir)
	{
		if(CWHACK.getFeatures().cameraNoClipFeature.isEnabled())
			cir.setReturnValue(desiredCameraDistance);
	}

	@Inject(at = {@At("HEAD")},
			method = {
					"getSubmersionType()Lnet/minecraft/client/render/CameraSubmersionType;"},
			cancellable = true)
	private void onGetSubmersionType(
			CallbackInfoReturnable<CameraSubmersionType> cir)
	{
		if(CWHACK.getFeatures().noOverlayFeature.isEnabled())
			cir.setReturnValue(CameraSubmersionType.NONE);
	}
}
