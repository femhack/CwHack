package net.cwhack.events;

import net.cwhack.event.CancellableEvent;
import net.cwhack.event.Listener;

import java.util.ArrayList;

public interface IsFullCubeListener extends Listener
{
	void onIsFullCube(IsFullCubeEvent event);

	class IsFullCubeEvent extends CancellableEvent<IsFullCubeListener>
	{

		@Override
		public void fire(ArrayList<IsFullCubeListener> listeners)
		{
			for (IsFullCubeListener listener : listeners)
			{
				listener.onIsFullCube(this);
				if (isCancelled())
					return;
			}
		}

		@Override
		public Class<IsFullCubeListener> getListenerType()
		{
			return IsFullCubeListener.class;
		}
	}
}
