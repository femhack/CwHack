package net.cwhack.features;

import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;
import net.cwhack.setting.BooleanSetting;
import net.cwhack.setting.DecimalSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import static net.cwhack.CwHack.MC;

public class TriggerBotFeature extends Feature implements UpdateListener
{

	private final DecimalSetting cooldown = new DecimalSetting("cooldown", "the required cooldown progress for the next attack", 1, this);

	private final BooleanSetting attackInAir = new BooleanSetting("attackInAir", "whether or not to attack target when they are in mid air", true, this);
	private final BooleanSetting attackOnJump = new BooleanSetting("attackOnJump", "whether or not to attack when you are jumping", true, this);

	public TriggerBotFeature()
	{
		super("TriggerBot", "automatically attack entity you are aiming at");
		addSetting(cooldown);
		addSetting(attackInAir);
		addSetting(attackOnJump);
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

	@Override
	public void onUpdate()
	{
		if (MC.player.isUsingItem())
			return;
		if (!MC.player.isHolding(item -> item.getItem() instanceof SwordItem))
			return;
		HitResult hit = MC.crosshairTarget;
		if (hit.getType() != HitResult.Type.ENTITY)
			return;
		if (MC.player.getAttackCooldownProgress(0) < cooldown.getValue())
			return;
		Entity target = ((EntityHitResult) hit).getEntity();
		if (!(target instanceof PlayerEntity))
			return;
		if (!target.isOnGround() && !attackInAir.getValue())
			return;
		if (MC.player.getY() > MC.player.prevY && !attackOnJump.getValue())
			return;
		MC.interactionManager.attackEntity(MC.player, target);
		MC.player.swingHand(Hand.MAIN_HAND);
	}
}
