package net.cwhack.events;

import net.cwhack.event.CancellableEvent;
import net.cwhack.event.Listener;

import java.util.ArrayList;

public interface SendMovementPacketsListener extends Listener
{
	void onSendMovementPackets(SendMovementPacketsEvent event);

	class SendMovementPacketsEvent extends CancellableEvent<SendMovementPacketsListener>
	{

		@Override
		public void fire(ArrayList<SendMovementPacketsListener> listeners)
		{
			for (SendMovementPacketsListener listener : listeners)
			{
				listener.onSendMovementPackets(this);
				if (isCancelled())
					return;
			}
		}

		@Override
		public Class<SendMovementPacketsListener> getListenerType()
		{
			return SendMovementPacketsListener.class;
		}
	}
}
