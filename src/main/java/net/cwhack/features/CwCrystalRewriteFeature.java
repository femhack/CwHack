package net.cwhack.features;

import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;
import net.cwhack.setting.IntegerSetting;
import net.cwhack.utils.CrystalUtils;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;

import static net.cwhack.CwHack.CWHACK;
import static net.cwhack.CwHack.MC;

public class CwCrystalRewriteFeature extends Feature implements UpdateListener
{

	private final IntegerSetting crystalBreakInterval = new IntegerSetting("crystalBreakInterval", "the speed of attacking the crystal", 0);

	private int crystalBreakClock = 0;

	public CwCrystalRewriteFeature()
	{
		super("CwCrystalRewrite", "rewriting for vulcan bypass");
		addSetting(crystalBreakInterval);
	}

	@Override
	public void onEnable()
	{
		crystalBreakClock = 0;
		eventManager.add(UpdateListener.class, this);
	}

	@Override
	public void onDisable()
	{
		eventManager.remove(UpdateListener.class, this);
	}

	private boolean isDeadBodyNearby()
	{
		return MC.world.getPlayers().parallelStream()
				.filter(e -> MC.player != e)
				.filter(e -> e.squaredDistanceTo(MC.player) < 36)
				.anyMatch(e -> e.isDead());
	}

	@Override
	public void onUpdate()
	{
		boolean dontBreakCrystal = crystalBreakClock != 0;
		if (dontBreakCrystal)
			crystalBreakClock--;
		CrystalAuraFeature ca = CWHACK.getFeatures().crystalAuraFeature;
		if (!MC.player.isHolding(Items.END_CRYSTAL))
			return;
		if (isDeadBodyNearby())
			return;
		if (MC.crosshairTarget instanceof BlockHitResult hit)
		{
			BlockPos block = hit.getBlockPos();
			if (CrystalUtils.canPlaceCrystalServer(block))
			{
				ItemStack mainHandStack = MC.player.getMainHandStack();
				boolean isMainHand = mainHandStack.isOf(Items.END_CRYSTAL);
				if (isMainHand || mainHandStack.isEmpty())
				{
					Hand hand = isMainHand ? Hand.MAIN_HAND : Hand.OFF_HAND;
					ActionResult result = MC.interactionManager.interactBlock(MC.player, MC.world, hand, hit);
					MC.interactionManager.interactItem(MC.player, MC.world, hand);

					if (result == ActionResult.SUCCESS)
						MC.player.swingHand(hand);
				}
			}
		}
		if (MC.crosshairTarget instanceof EntityHitResult hit)
		{
			if (hit.getEntity() instanceof EndCrystalEntity crystal)
			{
				if (!dontBreakCrystal)
				{
					crystalBreakClock = crystalBreakInterval.getValue();
					MC.interactionManager.attackEntity(MC.player, crystal);
					MC.player.swingHand(Hand.MAIN_HAND);
					CWHACK.getCrystalDataTracker().recordAttack(crystal);
				}
			}
		}
	}
}
