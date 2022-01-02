package net.cwhack.features;

import net.cwhack.events.KeyPressListener;
import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;
import net.cwhack.setting.IntegerSetting;
import net.cwhack.utils.BlockUtils;
import net.cwhack.utils.InventoryUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;
import java.util.stream.StreamSupport;

import static net.cwhack.CwHack.CWHACK;
import static net.cwhack.CwHack.MC;

public class AutoCrystalFeature extends Feature implements UpdateListener, KeyPressListener
{

	private CrystalAuraFeature crystalAura;
	private FastBreakFeature fastBreak;
	private AutoCityFeature autoCity;
	private KillAuraFeature killAura;
	private AnchorAuraFeature anchorAura;

	private final IntegerSetting killAuraHotkey = new IntegerSetting("killauraHotkey", "killaura hotkey", -1);
	private boolean usingKillaura = false;

	private final IntegerSetting gapHotkey = new IntegerSetting("gapHotkey", "gap hotkey", -1);
	private boolean gapping = false;

	private final IntegerSetting chorusHotkey = new IntegerSetting("chorusHotkey", "chorus hotkey", -1);
	private boolean usingChorusFruit = false;

	private AutoCrystalState state = AutoCrystalState.CRYSTALLING;

	public AutoCrystalFeature()
	{
		super("AutoCrystal", "hvh bot");
		addSetting(killAuraHotkey);
		addSetting(gapHotkey);
		addSetting(chorusHotkey);
	}

	@Override
	protected void onEnable()
	{
		getFeatures();
		usingKillaura = false;
		eventManager.add(UpdateListener.class, this, 500);
		eventManager.add(KeyPressListener.class, this);
	}

	@Override
	protected void onDisable()
	{
		eventManager.remove(UpdateListener.class, this);
		eventManager.remove(KeyPressListener.class, this);
	}

	private void getFeatures()
	{
		crystalAura = CWHACK.getFeatures().crystalAuraFeature;
		fastBreak = CWHACK.getFeatures().fastBreakFeature;
		autoCity = CWHACK.getFeatures().autoCityFeature;
		killAura = CWHACK.getFeatures().killAuraFeature;
		anchorAura = CWHACK.getFeatures().anchorAuraFeature;
	}

	private Entity findTarget()
	{
		return StreamSupport.stream(MC.world.getEntities().spliterator(), true)
				.filter(e -> e != MC.player)
				.filter(e -> !e.isRemoved())
				.filter(e -> e instanceof PlayerEntity)
				.filter(e -> ((PlayerEntity) e).getHealth() > 0.0f)
				//.filter(e -> MC.player.squaredDistanceTo(e) <= range.getValue() * range.getValue())
				.min(Comparator.comparingDouble(e -> MC.player.squaredDistanceTo(e))).orElse(null);
	}

	@Override
	public void onKeyPress(KeyPressEvent event)
	{
		if (MC.currentScreen != null)
			return;

		int keyCode = event.getKeyCode();
		int action = event.getAction();

		if (keyCode == killAuraHotkey.getValue())
		{
			if (action == GLFW.GLFW_PRESS)
				usingKillaura = true;
			if (action == GLFW.GLFW_RELEASE)
				usingKillaura = false;
		}
		if (keyCode == gapHotkey.getValue())
		{
			if (action == GLFW.GLFW_PRESS)
				gapping = true;
			if (action == GLFW.GLFW_RELEASE)
			{
				gapping = false;
				MC.options.keyUse.setPressed(false);
			}
		}
		if (keyCode == chorusHotkey.getValue())
		{
			if (action == GLFW.GLFW_PRESS)
				usingChorusFruit = true;
			if (action == GLFW.GLFW_RELEASE)
			{
				usingChorusFruit = false;
				MC.options.keyUse.setPressed(false);
			}
		}
	}

	@Override
	public void onUpdate()
	{
		Entity target = findTarget();
		if (gapping)
		{
			InventoryUtils.selectItemFromHotbar(Items.ENCHANTED_GOLDEN_APPLE);
			MC.options.keyUse.setPressed(true);
		}
		else if (usingChorusFruit)
		{
			InventoryUtils.selectItemFromHotbar(Items.CHORUS_FRUIT);
			MC.options.keyUse.setPressed(true);
		}
		if (target == null)
		{
			crystalAura.setEnabled(false);
			anchorAura.setEnabled(false);
			killAura.setEnabled(false);
			autoCity.setEnabled(false);
		}
		crystalAura.overrideTarget(target);
		anchorAura.overrideTarget(target);
		autoCity.overrideTarget(target);
		killAura.overrideTarget(target);
		if (target == null)
			return;
		if (gapping || usingChorusFruit)
		{
			crystalAura.setEnabled(false);
			autoCity.setEnabled(false);
			anchorAura.setEnabled(false);
			if (fastBreak.isMining())
				fastBreak.cancelMining();
		}
		else if (MC.player.isHolding(Items.EXPERIENCE_BOTTLE))
		{
			crystalAura.setEnabled(false);
			autoCity.setEnabled(false);
			anchorAura.setEnabled(false);
			if (fastBreak.isMining())
				fastBreak.cancelMining();
		}
		else if (usingKillaura)
		{
			killAura.setEnabled(true);
			crystalAura.setEnabled(false);
			autoCity.setEnabled(false);
			anchorAura.setEnabled(false);
			if (fastBreak.isMining())
				fastBreak.cancelMining();
		}
		else if (!MC.world.getDimension().isRespawnAnchorWorking() && CWHACK.getFeatures().holeEspFeature.isSurrounded(target.getBlockPos()) && (!BlockUtils.hasBlock(target.getBlockPos().up(2)) || BlockUtils.isBlock(Blocks.RESPAWN_ANCHOR, target.getBlockPos().up(2))))
		{
			killAura.setEnabled(false);
			crystalAura.setEnabled(false);
			autoCity.setEnabled(false);
			anchorAura.setEnabled(true);
		}
		else if (autoCity.isCitying())
		{
			//if (!fastBreak.isEnabled())
				crystalAura.setEnabled(false);
		}
		else
		{
			killAura.setEnabled(false);
			crystalAura.setEnabled(true);
			autoCity.setEnabled(true);
			anchorAura.setEnabled(false);
		}
//		if (MC.player.isOnGround()) // anti bed aura
//		{
//			if (!MC.player.isHolding(itemStack -> itemStack.getItem() instanceof PotionItem || itemStack.getItem() instanceof EnchantedGoldenAppleItem))
//			{
//				if (CWHACK.getFeatures().holeEspFeature.isSurrounded(MC.player.getBlockPos()))
//				{
//					if (!BlockUtils.hasBlock(MC.player.getBlockPos()) || !BlockUtils.hasBlock(MC.player.getBlockPos().add(0, 1, 0)))
//					{
//						int slot = MC.player.getInventory().selectedSlot;
//						if (InventoryUtils.selectItemFromHotbar(item -> item == Items.ANVIL))
//						{
//							((IClientPlayerInteractionManager) MC.interactionManager).cwSyncSelectedSlot();
//							BlockPlacer.tryAirPlaceBlock(MC.player.getBlockPos().add(0, 2, 0));
//						}
//						MC.player.getInventory().selectedSlot = slot;
//					}
//				}
//			}
//		}
	}

	private enum AutoCrystalState
	{
		CRYSTALLING,
		CITYING,
		GAPPING
	}
}
