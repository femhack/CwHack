package net.cwhack.features;

import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;
import net.cwhack.setting.IntegerSetting;
import net.cwhack.utils.CrystalUtils;
import net.minecraft.entity.LivingEntity;
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

	private final IntegerSetting crystalPlaceInterval = new IntegerSetting("crystalPlaceInterval", "the speed of placing the crystal", 0, this);
	private final IntegerSetting crystalBreakInterval = new IntegerSetting("crystalBreakInterval", "the speed of attacking the crystal", 0, this);

	private int crystalPlaceClock = 0;
	private int crystalBreakClock = 0;

	public CwCrystalRewriteFeature()
	{
		super("CwCrystalRewrite", "rewriting for vulcan bypass");
	}

	@Override
	public void onEnable()
	{
		crystalPlaceClock = 0;
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
				.anyMatch(LivingEntity::isDead);
	}

	@Override
	public void onUpdate()
	{
		boolean dontPlaceCrystal = crystalPlaceClock != 0;
		boolean dontBreakCrystal = crystalBreakClock != 0;
		if (dontPlaceCrystal)
			crystalPlaceClock--;
		if (dontBreakCrystal)
			crystalBreakClock--;
		ItemStack mainHandStack = MC.player.getMainHandStack();
		if (!mainHandStack.isOf(Items.END_CRYSTAL))
			return;
		if (isDeadBodyNearby())
			return;

		if (MC.crosshairTarget instanceof BlockHitResult hit)
		{
			BlockPos block = hit.getBlockPos();
			if (!dontPlaceCrystal && CrystalUtils.canPlaceCrystalServer(block))
			{
				crystalPlaceClock = crystalPlaceInterval.getValue();
				ActionResult result = MC.interactionManager.interactBlock(MC.player, MC.world, Hand.MAIN_HAND, hit);
				if (result == ActionResult.SUCCESS)
					MC.player.swingHand(Hand.MAIN_HAND);
			}
		}
		if (MC.crosshairTarget instanceof EntityHitResult hit)
		{
			if (!dontBreakCrystal && hit.getEntity() instanceof EndCrystalEntity crystal)
			{
				crystalBreakClock = crystalBreakInterval.getValue();
				MC.interactionManager.attackEntity(MC.player, crystal);
				MC.player.swingHand(Hand.MAIN_HAND);
				CWHACK.getCrystalDataTracker().recordAttack(crystal);
			}
		}
	}
}
