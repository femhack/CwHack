package net.cwhack.features;

import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;
import net.cwhack.setting.BooleanSetting;
import net.cwhack.setting.DecimalSetting;
import net.cwhack.utils.*;
import net.minecraft.block.Blocks;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static net.cwhack.CwHack.MC;

public class AntiDoubleTapFeature extends Feature implements UpdateListener
{

	private final DecimalSetting range = new DecimalSetting("range", "how far does the enemy have to be in order to trigger it", 6.0, this);
	private final BooleanSetting stopOnUsingItem = new BooleanSetting("stopOnUsingItem", "whether or not to stop while eating or using item", false, this);
	private final DecimalSetting activatesAbove = new DecimalSetting("activatesAbove", "how much you need to leave the ground before activating it," +
			" set it to non-zero if it stops you from comboing in holes", 0.0, this);
	private final BooleanSetting predictCrystals = new BooleanSetting("predictCrystals", "whether or not to predict crystal placements", false, this);
	private final BooleanSetting checkEnemiesAim = new BooleanSetting("checkEnemiesAim", "whether or not to check your enemies' look angle when predicting", true, this);

	public AntiDoubleTapFeature()
	{
		super("AntiDoubleTap", "Automatically switch to totem when in danger");
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
		if (MC.world.getPlayers().parallelStream()
				.filter(e -> e != MC.player)
				.noneMatch(player -> MC.player.squaredDistanceTo(player) <= range.getValue() * range.getValue()))
			return;

		if (stopOnUsingItem.getValue() && MC.player.isUsingItem())
			return;

		double activatesAboveV = activatesAbove.getValue();
		int f = (int) Math.floor(activatesAboveV);
		for (int i = 1; i <= f; i++)
			if (BlockUtils.hasBlock(MC.player.getBlockPos().add(0, -i, 0)))
				return;
		if (BlockUtils.hasBlock(new BlockPos(MC.player.getPos().add(0, -activatesAboveV, 0))))
			return;

		List<EndCrystalEntity> crystals = getNearByCrystals();
		ArrayList<Vec3d> crystalsPos = new ArrayList<>();
		crystals.forEach(e -> crystalsPos.add(e.getPos()));

		if (predictCrystals.getValue())
		{
			Stream<BlockPos> stream =
					BlockUtils.getAllInBoxStream(MC.player.getBlockPos().add(-6, -8, -6), MC.player.getBlockPos().add(6, 2, 6))
							.filter(e -> BlockUtils.isBlock(Blocks.OBSIDIAN, e) || BlockUtils.isBlock(Blocks.BEDROCK, e))
							.filter(CrystalUtils::canPlaceCrystalClient);
			if (checkEnemiesAim.getValue())
				stream = stream.filter(this::arePeopleAimingAtBlock);
			stream.forEachOrdered(e -> crystalsPos.add(Vec3d.ofBottomCenter(e).add(0, 1, 0)));
		}

		for (Vec3d pos : crystalsPos)
		{
			double damage =
					DamageUtils.crystalDamage(MC.player, pos, true, null, false);
			if (damage >= MC.player.getHealth() + MC.player.getAbsorptionAmount())
			{
				InventoryUtils.selectItemFromHotbar(Items.TOTEM_OF_UNDYING);
				break;
			}
		}
	}

	private boolean arePeopleAimingAtBlock(BlockPos block)
	{
		return MC.world.getPlayers().parallelStream()
				.filter(e -> e != MC.player)
				.anyMatch(e ->
				{
					Vec3d eyesPos = RotationUtils.getEyesPos(e);
					BlockHitResult hitResult = MC.world.raycast(new RaycastContext(eyesPos, eyesPos.add(RotationUtils.getPlayerLookVec(e).multiply(4.5)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, e));
					return hitResult != null && hitResult.getBlockPos().equals(block);
				});
	}
}
