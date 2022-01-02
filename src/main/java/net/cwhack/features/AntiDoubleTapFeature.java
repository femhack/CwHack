package net.cwhack.features;

import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;
import net.cwhack.setting.DecimalSetting;
import net.cwhack.utils.DamageUtils;
import net.cwhack.utils.InventoryUtils;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

import static net.cwhack.CwHack.MC;

public class AntiDoubleTapFeature extends Feature implements UpdateListener
{

	private final DecimalSetting range = new DecimalSetting("range", "how far does the enemy have to be in order to trigger it", 4.0);

	public AntiDoubleTapFeature()
	{
		super("AntiDoubleTap", "Automatically switch to totem when in danger");
		addSetting(range);
	}

	@Override
	protected void onEnable()
	{
		eventManager.add(UpdateListener.class, this);
	}

	@Override
	protected void onDisable()
	{
		eventManager.remove(UpdateListener.class, this);
	}

	private List<EndCrystalEntity> getNearByCrystals()
	{
		Vec3d pos = MC.player.getPos();
		return MC.world.getEntitiesByClass(EndCrystalEntity.class, new Box(pos.add(-6, -6, -6), pos.add(6, 6, 6)), a -> true);
	}

	@Override
	public void onUpdate()
	{
		if (MC.world.getPlayers().parallelStream().noneMatch(player -> MC.player.squaredDistanceTo(player) <= range.getValue() * range.getValue()))
			return;
		List<EndCrystalEntity> crystals = getNearByCrystals();
		for (EndCrystalEntity crystal : crystals)
		{
			double damage =
					DamageUtils.crystalDamage(MC.player, crystal.getPos(), true, null, false);
			if (damage >= MC.player.getHealth() + MC.player.getAbsorptionAmount())
			{
				InventoryUtils.selectItemFromHotbar(Items.TOTEM_OF_UNDYING);
				break;
			}
		}
	}
}
