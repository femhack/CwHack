package net.cwhack.events;

import net.cwhack.event.Event;
import net.cwhack.event.Listener;

import java.util.ArrayList;

public interface FrameBeginListener extends Listener
{

	void onFrameBegin();

	class FrameBeginEvent extends Event<FrameBeginListener>
	{

		@Override
		public void fire(ArrayList<FrameBeginListener> listeners)
		{
			for (FrameBeginListener listener : listeners)
			{
				listener.onFrameBegin();
			}
		}

		@Override
		public Class<FrameBeginListener> getListenerType()
		{
			return FrameBeginListener.class;
		}
	}
}
