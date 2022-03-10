package net.cwhack.features;

import com.mojang.blaze3d.systems.RenderSystem;
import net.cwhack.events.KeyPressListener;
import net.cwhack.events.PostMotionListener;
import net.cwhack.events.RenderListener;
import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;
import net.cwhack.setting.DecimalSetting;
import net.cwhack.setting.IntegerSetting;
import net.cwhack.utils.*;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static net.cwhack.CwHack.CWHACK;
import static net.cwhack.CwHack.MC;

public class CrystalAuraFeature extends Feature implements UpdateListener, KeyPressListener, PostMotionListener, RenderListener
{

	private final IntegerSetting crystalBreakDelay = new IntegerSetting("crystalBreakDelay", "delay attacking the crystal after it is spawned", 0, this);
	private final IntegerSetting crystalBreakInterval = new IntegerSetting("crystalBreakInterval", "the speed of attacking the crystal", 0, this);
	private final IntegerSetting unmarkBrokenCrystalDelay = new IntegerSetting("unmarkBrokenCrystalDelay", "after a crystal is attacked, the crystalAttacked flag will be turned off again after this delay. Set to 0 to turn off.", 5, this);
	private final IntegerSetting crystalPlaceInterval = new IntegerSetting("crystalPlaceInterval", "the speed of placing the crystals", 0, this);
	private final DecimalSetting placeRange = new DecimalSetting("placeRange", "the attack and place range", 4.5, this);
	private final DecimalSetting breakRange = new DecimalSetting("breakRange", "the attack and place range", 4, this);
	private final DecimalSetting maxSelfDamage = new DecimalSetting("maxSelfDamage", "the maximum damage allowed to deal to yourself", 8, this);
	private final DecimalSetting minDamage = new DecimalSetting("minDamage", "the minimum damage allowed to deal to your enemy", 8, this);
	private final IntegerSetting allowSuicide = new IntegerSetting("allowSuicide", "allow the crystal to pop yourself or kys", 0, this);
	private final IntegerSetting facePlaceHotkey = new IntegerSetting("facePlaceHotkey", "when this key is down face place will be enabled, set to -1 to ignore this option", -1, this);

	private boolean isFacePlacing = false;

	private boolean attackingCrystal = false;
	private Entity crystalToAttack = null;
	private BlockHitResult placingOn = null;

	private boolean placingCrystal = false;

	private int crystalBreakClock;
	private int crystalPlaceClock;

	private Entity target = null;
	private boolean overridingTarget = false;

	public CrystalAuraFeature()
	{
		super("CrystalAura", "Automatically place and break crystal around your enemy");
	}

	@Override
	protected void onEnable()
	{
		eventManager.add(UpdateListener.class, this, 100);
		eventManager.add(KeyPressListener.class, this);
		eventManager.add(PostMotionListener.class, this);
		eventManager.add(RenderListener.class, this);
		crystalBreakClock = 0;
		crystalPlaceClock = 0;
	}

	@Override
	protected void onDisable()
	{
		eventManager.remove(UpdateListener.class, this);
		eventManager.remove(KeyPressListener.class, this);;
		eventManager.remove(PostMotionListener.class, this);
		eventManager.remove(RenderListener.class, this);
	}

	public void overrideTarget(Entity target)
	{
		overridingTarget = target != null;
		this.target = target;
	}

	@Override
	public void onUpdate()
	{
		if (!overridingTarget)
			target = findTarget();
		if (target == null)
			return;

		if (!MC.player.isOnGround())
			return;
		if (CWHACK.getRotationFaker().isFaking())
			return;
		if (MC.player.isUsingItem())
			return;

		ArrayList<Entity> nearbyCrystals = getNearbyCrystals();
		boolean bl = false;
		if (crystalBreakClock == 0)
		{
			bl = breakCrystals(nearbyCrystals);
			if (bl)
				crystalBreakClock = crystalBreakInterval.getValue();
		}
		else
			crystalBreakClock--;
		if (crystalPlaceClock == 0)
		{
			if (!bl)
			{
				if (placeCrystals())
					crystalPlaceClock = crystalPlaceInterval.getValue();
			}
		}
		else
			crystalPlaceClock--;
	}

	@Override
	public void onKeyPress(KeyPressEvent event)
	{
		if (MC.currentScreen != null)
			return;

		if (facePlaceHotkey.getValue() == -1)
		{
			isFacePlacing = false;
			return;
		}

		if (event.getKeyCode() == facePlaceHotkey.getValue())
		{
			if (event.getAction() == GLFW.GLFW_PRESS)
				isFacePlacing = true;
			if (event.getAction() == GLFW.GLFW_RELEASE)
				isFacePlacing = false;
		}
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

	private ArrayList<Entity> getNearbyCrystals()
	{
		return StreamSupport.stream(MC.world.getEntities().spliterator(), true)
				.filter(e -> !e.isRemoved())
				.filter(e -> e instanceof EndCrystalEntity)
				.filter(e -> MC.player.squaredDistanceTo(e) <= breakRange.getValue() * breakRange.getValue())
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private boolean breakCrystals(ArrayList<Entity> crystals)
	{
		Entity crystal = crystals.parallelStream()
				.filter(e -> CWHACK.getCrystalDataTracker().getCrystalAge(e) > crystalBreakDelay.getValue())
				.filter(e -> !CWHACK.getCrystalDataTracker().isCrystalAttacked(e))
				.filter(e ->
						DamageUtils.crystalDamage(MC.player, e.getPos(), true, null, false)
								<= maxSelfDamage.getValue())
				.max(Comparator.comparingDouble(e ->
						DamageUtils.crystalDamage((PlayerEntity) target, e.getPos(), true, null, false)))
				.orElse(null);

		if (crystal == null)
			return false;

		attackCrystal(crystal);

		return true;
	}

	private boolean placeCrystals()
	{
		BlockPos blockToPlace = BlockUtils.getAllInBoxStream(RotationUtils.getEyesBlockPos().add(-placeRange.getValue(), -placeRange.getValue(), -placeRange.getValue()),
				RotationUtils.getEyesBlockPos().add(placeRange.getValue(), placeRange.getValue(), placeRange.getValue()))
				.filter(CrystalUtils::canPlaceCrystalServer)
				.filter(block -> Vec3d.ofCenter(block).squaredDistanceTo(MC.player.getPos()) <= placeRange.getValue() * placeRange.getValue())
				.filter(block ->
				{
					Vec3d crystalPos = Vec3d.ofBottomCenter(block).add(0, 1, 0);
					double selfDamage = DamageUtils.crystalDamage(MC.player, crystalPos, true, null, false);
					return selfDamage <= maxSelfDamage.getValue();
				})
				.max(Comparator.comparingDouble(block ->
				{
					Vec3d crystalPos = Vec3d.ofBottomCenter(block).add(0, 1, 0);
					return DamageUtils.crystalDamage((PlayerEntity) target, crystalPos, true, null, false);
				})).orElse(null);

		if (blockToPlace == null)
			return doFacePlace();

		Vec3d crystalPos = Vec3d.ofBottomCenter(blockToPlace).add(0, 1, 0);
		double targetDamage = DamageUtils.crystalDamage((PlayerEntity) target, crystalPos, true, null, false);
		if (targetDamage < minDamage.getValue())
			return doFacePlace();

		return placeCrystal(blockToPlace);
	}

	private boolean doFacePlace()
	{
		if (isFacePlacing)
		{
			BlockPos blockToPlace = BlockUtils.getAllInBoxStream(RotationUtils.getEyesBlockPos().add(-placeRange.getValue(), -placeRange.getValue(), -placeRange.getValue()),
					RotationUtils.getEyesBlockPos().add(placeRange.getValue(), placeRange.getValue(), placeRange.getValue()))
					.filter(CrystalUtils::canPlaceCrystalServer)
					.filter(block -> Vec3d.ofCenter(block).squaredDistanceTo(MC.player.getPos()) <= placeRange.getValue() * placeRange.getValue())
					.filter(block ->
					{
						BlockPos targetPos = target.getBlockPos();
						return block.equals(targetPos.north())
								|| block.equals(targetPos.south())
								|| block.equals(targetPos.west())
								|| block.equals(targetPos.east());
					})
					.max(Comparator.comparingDouble(block ->
					{
						Vec3d crystalPos = Vec3d.ofBottomCenter(block).add(0, 1, 0);
						return DamageUtils.crystalDamage((PlayerEntity) target, crystalPos, true, null, false);
					})).orElse(null);
			if (blockToPlace != null)
			{
				return placeCrystal(blockToPlace);
			}
		}
		return false;
	}

	private void attackCrystal(Entity crystal)
	{
		Vec3d center = crystal.getBoundingBox().getCenter().add(0, -1, 0);
		CWHACK.getRotationFaker().setServerLookPos(center);
		crystalToAttack = crystal;
		attackingCrystal = true;
		CWHACK.getCrystalDataTracker().recordAttack(crystalToAttack);
	}

	public boolean placeCrystal(BlockPos block)
	{
		Vec3d eyesPos = RotationUtils.getEyesPos();
		Vec3d posVec = Vec3d.ofCenter(block);
		double distanceSqPosVec = eyesPos.squaredDistanceTo(posVec);
		for(Direction side : Direction.values())
		{
			Vec3d hitVec = posVec.add(Vec3d.of(side.getVector()).multiply(0.5));
			double distanceSqHitVec = eyesPos.squaredDistanceTo(hitVec);
			// check if side is facing towards player
			if(distanceSqHitVec >= distanceSqPosVec)
				continue;
			CWHACK.getRotationFaker().setServerLookPos(hitVec);
			if (!InventoryUtils.selectItemFromHotbar(item -> item == Items.END_CRYSTAL))
				return false;
			placingCrystal = true;
			placingOn = new BlockHitResult(hitVec, side, block, false);
			return true;
		}

		return false;
	}

	@Override
	public void onPostMotion()
	{
		if (attackingCrystal)
		{
			MC.interactionManager.attackEntity(MC.player, crystalToAttack);
			MC.player.swingHand(Hand.MAIN_HAND);
			attackingCrystal = false;
		}
		if (placingCrystal)
		{
			ActionResult result = MC.interactionManager.interactBlock(MC.player, MC.world, Hand.MAIN_HAND, placingOn);
			if (result.isAccepted())
				MC.player.swingHand(Hand.MAIN_HAND);
			placingCrystal = false;
		}
	}

	@Override
	public void onRender(RenderEvent event)
	{
		MatrixStack matrixStack = event.getMatrixStack();

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		matrixStack.push();
		RenderUtils.applyRegionalRenderOffset(matrixStack);
		BlockPos blockPos = RenderUtils.getCameraBlockPos();
		int regionX = (blockPos.getX() >> 9) * 512;
		int regionZ = (blockPos.getZ() >> 9) * 512;

		RenderSystem.setShader(GameRenderer::getPositionShader);

		if (placingOn != null && placingCrystal)
		{
			BlockPos placingOnBlock = placingOn.getBlockPos();
			matrixStack.push();
			matrixStack.translate(placingOnBlock.getX() - regionX, placingOnBlock.getY(), placingOnBlock.getZ() - regionZ);

			RenderSystem.setShaderColor(0.4f, 1.0f, 0.4f, 0.4f);
			RenderUtils.drawSolidBox(new Box(BlockPos.ORIGIN), matrixStack);

			matrixStack.pop();
		}

		if (crystalToAttack != null && !crystalToAttack.isRemoved())
		{
			matrixStack.push();
			matrixStack.translate(crystalToAttack.getX(), crystalToAttack.getY(), crystalToAttack.getZ());
			matrixStack.scale(crystalToAttack.getWidth(), crystalToAttack.getHeight(), crystalToAttack.getWidth());

			RenderSystem.setShaderColor(1.0f, 0.4f, 0.4f, 1.0f);
			Box bb = new Box(-0.5, 0, -0.5, 0.5, 1, 0.5);
			RenderUtils.drawOutlinedBox(bb, matrixStack);

			matrixStack.pop();
		}

		matrixStack.pop();
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}
}
