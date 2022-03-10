package net.cwhack.utils;

import net.cwhack.event.CancellableEvent;
import net.cwhack.event.Event;
import net.cwhack.event.EventManager;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public enum MixinUtils
{
	;
	public static void fireEvent(Event<?> event)
	{
		EventManager.fire(event);
	}

	public static void fireCancellableEvent(CancellableEvent<?> event, CallbackInfo ci)
	{
		EventManager.fire(event);
		if (event.isCancelled())
			ci.cancel();
	}

}
