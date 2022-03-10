package net.cwhack.features;

import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;
import net.cwhack.utils.BlockUtils;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

import static net.cwhack.CwHack.MC;

public class MarlowAnchorFeature extends Feature implements UpdateListener
{
	public MarlowAnchorFeature()
	{
		super("MarlowAnchor", "lalalalalalalalala alalala");
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
		if (MC.crosshairTarget instanceof BlockHitResult hit)
		{
			BlockPos pos = hit.getBlockPos();
			if (BlockUtils.isAnchorCharged(pos))
			{
				if (!MC.player.isHolding(Items.GLOWSTONE))
				{
					ActionResult actionResult = MC.interactionManager.interactBlock(MC.player, MC.world, Hand.MAIN_HAND, hit);
					if (actionResult.isAccepted() && actionResult.shouldSwingHand())
						MC.player.swingHand(Hand.MAIN_HAND);
				}
			}
		}
	}
}
