package net.cwhack.features;

import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;
import net.cwhack.utils.BlockUtils;
import net.cwhack.utils.InventoryUtils;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import static net.cwhack.CwHack.MC;

public class AutoAnchorFeature extends Feature implements UpdateListener
{

	private int state;

	public AutoAnchorFeature()
	{
		super("AutoAnchor", "Automatically place down an anchor and detonate for you");
	}

	@Override
	protected void onEnable()
	{
		state = 0;
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
		//anchor();
		// place anchor
		if (state == 0)
		{
			InventoryUtils.selectItemFromHotbar(item -> item == Items.RESPAWN_ANCHOR);
			if (!rightClick())
				setEnabled(false);
			state++;
			return;
		}
		if (state == 1)
		{
			InventoryUtils.selectItemFromHotbar(item -> item == Items.GLOWSTONE);
			if (!rightClick())
				setEnabled(false);
			state++;
			return;
		}
		if (state == 2)
		{
			InventoryUtils.selectItemFromHotbar(item -> item != Items.RESPAWN_ANCHOR);
//			if (!rightClick())
//				setEnabled(false);
			rightClick();
			setEnabled(false);
		}
	}

	private boolean rightClick()
	{
		HitResult hit = MC.crosshairTarget;
		if (hit == null)
			return false;
		if (MC.crosshairTarget.getType() != HitResult.Type.BLOCK)
			return false;
		BlockHitResult blockHit = (BlockHitResult) hit;
		// we don't care about this because it is always block item
		//ActionResult result1 = MC.interactionManager.interactItem(MC.player, MC.world, Hand.MAIN_HAND);
		ActionResult result2 = MC.interactionManager.interactBlock(MC.player, MC.world, Hand.MAIN_HAND, blockHit);
		if (result2 == ActionResult.SUCCESS)
		{
			MC.player.swingHand(Hand.MAIN_HAND);
			return true;
		}
		return false;
	}

	private void anchor()
	{
		HitResult hit = MC.crosshairTarget;
		if (hit == null)
			return;
		if (MC.crosshairTarget.getType() != HitResult.Type.BLOCK)
			return;

		MinecraftClient mc = MC;

		BlockHitResult blockHit = (BlockHitResult) hit;
		BlockPos blockPos = blockHit.getBlockPos();

		boolean isAnchorAlreadyThere = BlockUtils.isBlock(Blocks.RESPAWN_ANCHOR, blockPos);

		BlockPos anchorPos;

		if (BlockUtils.getBlockState(blockPos).getMaterial().isReplaceable() || isAnchorAlreadyThere)
			anchorPos = blockPos;
		else
			anchorPos = blockPos.add(blockHit.getSide().getVector());

		ActionResult result;

		InventoryUtils.selectItemFromHotbar(i -> i == Items.RESPAWN_ANCHOR);

		if (!isAnchorAlreadyThere)
		{
			result = mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, blockHit);
			if (result == ActionResult.SUCCESS)
				mc.player.swingHand(Hand.MAIN_HAND);
		}

		BlockHitResult anchorBlockHit = BlockUtils.clientRaycastBlock(anchorPos);

		if (anchorBlockHit == null)
			return;

		InventoryUtils.selectItemFromHotbar(i -> i == Items.GLOWSTONE);
		result = mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, anchorBlockHit);
		if (result == ActionResult.SUCCESS)
			mc.player.swingHand(Hand.MAIN_HAND);

		InventoryUtils.selectItemFromHotbar(i -> i != Items.GLOWSTONE);
		result = mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, anchorBlockHit);
		if (result == ActionResult.SUCCESS)
			mc.player.swingHand(Hand.MAIN_HAND);
	}
}
