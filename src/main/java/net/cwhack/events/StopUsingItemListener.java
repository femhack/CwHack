package net.cwhack.events;

import net.cwhack.event.CancellableEvent;
import net.cwhack.event.Listener;

import java.util.ArrayList;

public interface StopUsingItemListener extends Listener
{
	void onStopUsingItem(StopUsingItemEvent event);

	class StopUsingItemEvent extends CancellableEvent<StopUsingItemListener>
	{

		@Override
		public void fire(ArrayList<StopUsingItemListener> listeners)
		{
			for (StopUsingItemListener listener : listeners)
				listener.onStopUsingItem(this);
		}

		@Override
		public Class<StopUsingItemListener> getListenerType()
		{
			return StopUsingItemListener.class;
		}
	}
}
