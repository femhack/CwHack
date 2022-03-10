package net.cwhack.events;

import net.cwhack.event.Event;
import net.cwhack.event.Listener;

import java.util.ArrayList;

public interface PreActionListener extends Listener
{
	void onPreAction();

	class PreActionEvent extends Event<PreActionListener>
	{

		@Override
		public void fire(ArrayList<PreActionListener> listeners)
		{
			for (PreActionListener listener : listeners)
			{
				listener.onPreAction();
			}
		}

		@Override
		public Class<PreActionListener> getListenerType()
		{
			return PreActionListener.class;
		}
	}
}
