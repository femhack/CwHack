package net.cwhack.features;

import net.cwhack.BlockPlacer;
import net.cwhack.events.RenderListener;
import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;
import net.cwhack.setting.DecimalSetting;
import net.cwhack.setting.IntegerSetting;
import net.cwhack.utils.BlockUtils;
import net.cwhack.utils.DamageUtils;
import net.cwhack.utils.InventoryUtils;
import net.cwhack.utils.RotationUtils;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static net.cwhack.CwHack.CWHACK;
import static net.cwhack.CwHack.MC;

public class AnchorAuraFeature extends Feature implements UpdateListener, RenderListener
{

	private final DecimalSetting range = new DecimalSetting("range", "range", 4.5, this);
	private final DecimalSetting maxSelfDamage = new DecimalSetting("maxSelfDamage", "the maximum self damage you will take from it", 8.0, this);
	private final IntegerSetting cooldown = new IntegerSetting("cooldown", "cooldown", 10, this);

	private Entity target;
	private boolean overridingTarget = false;

	private int clock = 0;

	public AnchorAuraFeature()
	{
		super("AnchorAura", "anchor anchor meow meow meow");
		addSetting(range);
		addSetting(maxSelfDamage);
		addSetting(cooldown);
	}

	@Override
	public void onEnable()
	{
		eventManager.add(UpdateListener.class, this, 50);
		eventManager.add(RenderListener.class, this);
	}

	@Override
	public void onDisable()
	{
		eventManager.remove(UpdateListener.class, this);
		eventManager.remove(RenderListener.class, this);
	}

	public void overrideTarget(Entity target)
	{
		overridingTarget = target != null;
		this.target = target;
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

	private ArrayList<BlockPos> getNearByAnchors()
	{
		BlockPos pos = RotationUtils.getEyesBlockPos();
		int reach = MathHelper.ceil(range.getValue());
		BlockPos r = new BlockPos(reach, reach, reach);
		return BlockUtils.getAllInBoxStream(pos.subtract(r), pos.add(r))
				.filter(block -> BlockUtils.isBlock(Blocks.RESPAWN_ANCHOR, block))
				.filter(block -> BlockUtils.isBlockReachable(block, range.getValue()))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private boolean isCharged(BlockPos anchor)
	{
		try
		{
			return BlockUtils.getBlockState(anchor).get(RespawnAnchorBlock.CHARGES) != 0;
		}
		catch (IllegalArgumentException e)
		{
			return false;
		}
	}

	@Override
	public void onUpdate()
	{
		if (!overridingTarget)
			target = findTarget();
		if (target == null)
			return;

		if (CWHACK.getRotationFaker().isFaking())
			return;
		if (MC.player.isUsingItem())
			return;

		ArrayList<BlockPos> anchors = getNearByAnchors();
		BlockPos anchor = anchors.stream()
				.filter(block -> DamageUtils.anchorDamage(MC.player, Vec3d.ofCenter(block)) <= maxSelfDamage.getValue())
				.max(Comparator.comparingDouble(block -> DamageUtils.anchorDamage((LivingEntity) target, Vec3d.ofCenter(block))))
				.orElse(null);

		if (anchor == null)
		{
			BlockPos above = target.getBlockPos().up(2);
			if (!BlockUtils.hasBlock(target.getBlockPos().up(2)) &&
					BlockUtils.isBlockReachable(above, range.getValue()) &&
					DamageUtils.bedDamage(MC.player, Vec3d.ofCenter(above)) <= maxSelfDamage.getValue())
				anchor = above;
			else
				return;

			if (!InventoryUtils.selectItemFromHotbar(Items.RESPAWN_ANCHOR))
				return;
			if (!BlockPlacer.placeBlock(anchor))
				BlockPlacer.tryAirPlaceBlock(anchor);
		}

		if (clock != 0)
		{
			clock--;
			return;
		}
		clock = cooldown.getValue();

		if (!isCharged(anchor))
		{
			if (!InventoryUtils.selectItemFromHotbar(Items.GLOWSTONE))
				return;
			BlockUtils.rightClickBlock(anchor);
		}
		if (!InventoryUtils.selectItemFromHotbar(item -> item != Items.GLOWSTONE))
			return;
		BlockUtils.rightClickBlock(anchor);
		if (!InventoryUtils.selectItemFromHotbar(item -> item != Items.RESPAWN_ANCHOR))
			return;
		Vec3d faceCenterToClick = Vec3d.ofBottomCenter(anchor).add(0, 1, 0);
		MC.getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(faceCenterToClick, Direction.UP, anchor, false)));
	}

	@Override
	public void onRender(RenderEvent event)
	{

	}
}
