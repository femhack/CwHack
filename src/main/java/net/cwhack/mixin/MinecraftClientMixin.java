package net.cwhack.mixin;

import net.cwhack.event.EventManager;
import net.cwhack.events.GameLeaveListener.GameLeaveEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin
{
	@Shadow
	public ClientWorld world;

	@Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
	private void onDisconnect(Screen screen, CallbackInfo info) {
		if (world != null) {
			EventManager.fire(new GameLeaveEvent());
		}
	}
}
