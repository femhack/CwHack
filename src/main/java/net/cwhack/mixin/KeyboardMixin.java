package net.cwhack.mixin;

import net.cwhack.event.EventManager;
import net.cwhack.events.KeyPressListener.KeyPressEvent;
import net.cwhack.mixinterface.IKeyboard;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin implements IKeyboard
{
	@Inject(at = @At("HEAD"), method = "onKey(JIIII)V")
	private void onOnKey(long windowHandle, int keyCode, int scanCode,
	                     int action, int modifiers, CallbackInfo ci)
	{
		KeyPressEvent event = new KeyPressEvent(keyCode, scanCode, action, modifiers);

		EventManager.fire(event);
	}

	@Shadow
	private void onChar(long window, int codePoint, int modifiers)
	{

	}

	@Override
	public void cwOnChar(long window, int codePoint, int modifiers)
	{
		onChar(window, codePoint, modifiers);
	}
}