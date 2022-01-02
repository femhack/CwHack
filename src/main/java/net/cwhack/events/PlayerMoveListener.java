package net.cwhack.events;

import net.cwhack.event.CancellableEvent;
import net.cwhack.event.Listener;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public interface PlayerMoveListener extends Listener
{
	void onPlayerMove(PlayerMoveEvent event);

	class PlayerMoveEvent extends CancellableEvent<PlayerMoveListener>
	{

		private MovementType movementType;
		private Vec3d movement;

		public PlayerMoveEvent(MovementType movementType, Vec3d movement)
		{
			this.movementType = movementType;
			this.movement = movement;
		}

		public MovementType getMovementType()
		{
			return movementType;
		}

		public Vec3d getMovement()
		{
			return movement;
		}

		public void setMovementType(MovementType movementType)
		{
			this.movementType = movementType;
		}

		public void setMovement(Vec3d movement)
		{
			this.movement = movement;
		}

		@Override
		public void fire(ArrayList<PlayerMoveListener> listeners)
		{
			for (PlayerMoveListener listener : listeners)
			{
				listener.onPlayerMove(this);
				if (isCancelled())
					return;
			}
		}

		@Override
		public Class<PlayerMoveListener> getListenerType()
		{
			return PlayerMoveListener.class;
		}
	}
}
