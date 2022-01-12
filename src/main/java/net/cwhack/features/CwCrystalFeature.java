package net.cwhack.features;

import net.cwhack.CwHack;
import net.cwhack.events.PostMotionListener;
import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;
import net.cwhack.setting.DecimalSetting;
import net.cwhack.setting.IntegerSetting;
import net.cwhack.utils.BlockUtils;
import net.cwhack.utils.RotationUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.Random;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static net.cwhack.CwHack.MC;

public class CwCrystalFeature extends Feature implements UpdateListener, PostMotionListener
{

	private final DecimalSetting range = new DecimalSetting("range", "the maximum distance you will attack a crystal", 4, this);
	private final DecimalSetting maxAngle = new DecimalSetting("maxAngle", "the maximum angle you can flick on a crystal", 60, this);
	private final IntegerSetting cooldown = new IntegerSetting("cooldown", "cooldown between each attack", 0, this);
	private final IntegerSetting placeDelay = new IntegerSetting("placeDelay", "place cooldown after attacking the crystal", 0, this);

	private Random rand = new Random();

	private Entity target;
	private boolean attacked;
	private boolean placed;
	private int clock;
	private int placeDelayClock;

	public CwCrystalFeature()
	{
		super("CwCrystal", "Automatically place and break nearby crystals");
	}

	@Override
	protected void onEnable()
	{
		target = null;
		attacked = false;
		placed = false;
		placeDelayClock = 0;
		clock = cooldown.getValue();
		eventManager.add(UpdateListener.class, this);
		eventManager.add(PostMotionListener.class, this);
	}

	@Override
	protected void onDisable()
	{
		eventManager.remove(UpdateListener.class, this);
		eventManager.remove(PostMotionListener.class, this);
	}

	@Override
	public void onUpdate()
	{
		if (target != null && target.isRemoved())
		{
			attacked = false;
			target = null;
		}

		if (MC.crosshairTarget.getType() == HitResult.Type.BLOCK)
		{
			BlockHitResult blockHit = (BlockHitResult) MC.crosshairTarget;
			Block block = BlockUtils.getBlock(blockHit.getBlockPos());
			if (block != Blocks.OBSIDIAN && block != Blocks.BEDROCK)
				placed = false;
		}
		else placed = false;

		ClientPlayerEntity player = MC.player;
		if (!player.getMainHandStack().isOf(Items.END_CRYSTAL))
			return;

		if (MC.currentScreen != null)
		{
			if (clock > 0)
				clock--;
			if (placeDelayClock > 0)
				placeDelayClock--;
			return;
		}

		if (clock <= 0)
		{
			Stream<Entity> playerStream =
					StreamSupport.stream(MC.world.getEntities().spliterator(), true)
							.filter(e -> e instanceof PlayerEntity)
							.filter(e -> e != MC.player);
			Entity closestPlayer = playerStream.min(Comparator.comparingDouble(e -> MC.player.squaredDistanceTo(e))).orElse(null);
			if (closestPlayer != null && MC.player.squaredDistanceTo(closestPlayer) < 36.0 && !closestPlayer.isAlive())
				return;

			double rangeSq = range.getValue() * range.getValue();
			Stream<Entity> stream =
					StreamSupport.stream(MC.world.getEntities().spliterator(), true)
							.filter(e -> !e.isRemoved())
							.filter(e -> e instanceof EndCrystalEntity)
							.filter(e -> e.squaredDistanceTo(RotationUtils.getEyesPos()) < rangeSq)
							.filter(e -> RotationUtils.getAngleToLookVec(e.getPos()) < maxAngle.getValue());
			Entity prev = target;
			target = stream.min(Comparator.comparingDouble(e -> RotationUtils.getAngleToLookVec(e.getBoundingBox().getCenter()))).orElse(null);
			attacked = prev == target;
			if (!attacked && target != null)
			{
				CwHack.CWHACK.getRotationFaker().setServerLookPos(getLookAtPos((EndCrystalEntity) target));
				clock = cooldown.getValue();
				placed = false;
				return;
			}
		}

		// ac bypass for vulcan
		// -------------------
//		if (placed && attacked)
//		{
//			if (target != null)
//			{
//				CWHACK.getRotationFaker().setServerLookPos(getLookAtPos((EndCrystalEntity) target));
//				ChatUtils.info("1");
//			}
//			else if (placeDelayClock > 1)
//			{
//				HitResult hit = MC.crosshairTarget;
//				if (hit.getType() == HitResult.Type.BLOCK)
//				{
//					BlockHitResult blockHit = (BlockHitResult) hit;
//					BlockPos pos = blockHit.getBlockPos();
//					Vec3d lookAt = Vec3d.ofCenter(pos)
//									.add(0.0, 0.5, 0.0);
//					CWHACK.getRotationFaker().setServerLookPos(lookAt);
//				}
//				ChatUtils.info("2");
//			}
//		}
		// -------------------

		clock--;

		if (placed)
			return;

		placeDelayClock--;

		if (placeDelayClock > 0)
			return;

		HitResult hit = MC.crosshairTarget;

		if (hit.getType() == HitResult.Type.BLOCK)
		{
			BlockHitResult blockHit = (BlockHitResult) hit;
			BlockPos blockPos = blockHit.getBlockPos();
			BlockState blockState = MC.world.getBlockState(blockPos);
			if (blockState.getBlock() == Blocks.OBSIDIAN || blockState.getBlock() == Blocks.BEDROCK)
			{
				MC.interactionManager.interactBlock(player, MC.world, Hand.MAIN_HAND, blockHit);
				ActionResult result = MC.interactionManager.interactItem(player, MC.world, Hand.MAIN_HAND);
				if (result == ActionResult.SUCCESS)
					player.swingHand(Hand.MAIN_HAND);
				placed = true;
				placeDelayClock = placeDelay.getValue();
				return;
			}
		}
	}

	@Override
	public void onPostMotion()
	{
		if (target == null)
			return;
		if (attacked)
			return;
		MC.interactionManager.attackEntity(MC.player, target);
		MC.player.swingHand(Hand.MAIN_HAND);
	}

	private Vec3d getLookAtPos(EndCrystalEntity crystal)
	{
		Vec3d offset = new Vec3d(rand.nextDouble(), rand.nextDouble(), rand.nextDouble())
				.subtract(0.5, 0.5, 0.5)
				.multiply(0.5);

		Vec3d lookAt = target.getPos()
				// make the lower boundary of the random number above the bottom of the crystal hit box
				.add(0.0, 0.25, 0.0)
				.add(offset);

		return lookAt;
	}
}
