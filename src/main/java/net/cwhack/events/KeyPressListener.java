package net.cwhack.events;

import net.cwhack.event.CancellableEvent;
import net.cwhack.event.Listener;

import java.util.ArrayList;

public interface KeyPressListener extends Listener
{
	void onKeyPress(KeyPressEvent event);

	class KeyPressEvent extends CancellableEvent<KeyPressListener>
	{

		private int keyCode, scanCode, action, modifiers;

		public KeyPressEvent(int keyCode, int scanCode, int action, int modifiers)
		{
			this.keyCode = keyCode;
			this.scanCode = scanCode;
			this.action = action;
			this.modifiers = modifiers;
		}

		@Override
		public void fire(ArrayList<KeyPressListener> listeners)
		{
			for (KeyPressListener listener : listeners)
				listener.onKeyPress(this);
		}

		@Override
		public Class<KeyPressListener> getListenerType()
		{
			return KeyPressListener.class;
		}

		public int getKeyCode()
		{
			return keyCode;
		}

		public int getScanCode()
		{
			return scanCode;
		}

		public int getAction()
		{
			return action;
		}

		public int getModifiers()
		{
			return modifiers;
		}
	}
}
