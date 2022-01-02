package net.cwhack.events;

import net.cwhack.event.Event;
import net.cwhack.event.Listener;

import java.util.ArrayList;

public interface IsPlayerInLavaListener extends Listener
{
	void onIsPlayerInLava(IsPlayerInLavaEvent event);

	class IsPlayerInLavaEvent extends Event<IsPlayerInLavaListener>
	{

		private boolean inLava;

		public IsPlayerInLavaEvent(boolean inLava)
		{
			this.inLava = inLava;
		}

		public boolean isInLava()
		{
			return inLava;
		}

		public void setInLava(boolean inLava)
		{
			this.inLava = inLava;
		}

		@Override
		public void fire(ArrayList<IsPlayerInLavaListener> listeners)
		{
			for (IsPlayerInLavaListener listener : listeners)
			{
				listener.onIsPlayerInLava(this);
			}
		}

		@Override
		public Class<IsPlayerInLavaListener> getListenerType()
		{
			return IsPlayerInLavaListener.class;
		}
	}
}
