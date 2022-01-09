package net.cwhack.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.StreamSupport;

import static net.cwhack.CwHack.MC;

public enum TargetUtils
{
	;
	public static Entity getTarget(TargetFilterConfiguration config)
	{
		return StreamSupport.stream(MC.world.getEntities().spliterator(), true)
				.filter(e -> e != MC.player)
				.filter(e -> !e.isRemoved())
				.filter(e -> e instanceof LivingEntity && !((LivingEntity) e).isDead() || e instanceof EndCrystalEntity)
				.filter(e -> !(config.filterPlayer && !(e instanceof PlayerEntity)))
				.filter(e -> !(config.filterAnimal && !(e instanceof AnimalEntity)))
				.filter(e -> !(config.filterMob && !(e instanceof Monster)))
				.filter(e -> !(config.filterBaby && !(e instanceof PassiveEntity && ((PassiveEntity) e).isBaby())))
				.filter(e -> !(config.filterNamed && !e.hasCustomName()))
				.filter(e -> !(config.filterTamed && !(e instanceof TameableEntity && ((TameableEntity) e).isTamed() || e instanceof HorseBaseEntity && ((HorseBaseEntity) e).isTame())))
				.filter(e -> !(config.filterTrader && !(e instanceof MerchantEntity)))
				.filter(e -> !(config.filterCrystal && !(e instanceof EndCrystalEntity)))
				.min(config.type.comparator)
				.orElse(null);
	}

	public static class TargetFilterConfiguration
	{

		public Type type = Type.DISTANCE;
		public boolean filterPlayer = false;
		public boolean filterAnimal = false;
		public boolean filterMob = false;
		public boolean filterBaby = false;
		public boolean filterNamed = false;
		public boolean filterTamed = false;
		public boolean filterTrader = false;
		public boolean filterCrystal = false;
		public ArrayList<Class<?>> filteredEntities = new ArrayList<>();

		public enum Type
		{
			HEALTH(Comparator.comparingDouble(e ->
			{
				if (e instanceof LivingEntity a)
				{
					return a.getHealth() + a.getAbsorptionAmount();
				}
				return 0;
			})),
			DISTANCE(Comparator.comparingDouble(e -> e.squaredDistanceTo(MC.player))),
			ANGLE(Comparator.comparingDouble(e -> RotationUtils.getAngleToLookVec(e.getPos())));

			public final Comparator<? super Entity> comparator;

			Type(Comparator<? super Entity> comparator)
			{
				this.comparator = comparator;
			}
		}
	}
}
