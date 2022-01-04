package net.cwhack.features;

import com.mojang.blaze3d.systems.RenderSystem;
import net.cwhack.BlockBreaker;
import net.cwhack.BlockPlacer;
import net.cwhack.events.RenderListener;
import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;
import net.cwhack.mixinterface.IClientPlayerInteractionManager;
import net.cwhack.setting.DecimalSetting;
import net.cwhack.utils.*;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;

import static net.cwhack.CwHack.CWHACK;
import static net.cwhack.CwHack.MC;

public class AutoCityFeature extends Feature implements UpdateListener, RenderListener
{

	private final DecimalSetting range = new DecimalSetting("range", "the range it will city", 4.5);

	private Entity target;
	private BlockPos city;

	private boolean overridingTarget = false;

	public AutoCityFeature()
	{
		super("AutoCity", "Automatically break blocks around someone");
		addSetting(range);
	}

	@Override
	protected void onEnable()
	{
		eventManager.add(UpdateListener.class, this, 200);
		eventManager.add(RenderListener.class, this);
	}

	@Override
	protected void onDisable()
	{
		eventManager.remove(UpdateListener.class, this);
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

		if (MC.player.isUsingItem())
			return;

		if (!shouldCity())
		{
			city = null;
			return;
		}

		boolean bl = city == null;
		if (city == null)
			city = findCity();
		if (city == null)
			return;
		if (bl)
		{
			preventPlacingBack();
			return;
		}

		if (!shouldKeepCity(city))
		{
			city = null;
			return;
		}

		InventoryUtils.selectItemFromHotbar(item -> item instanceof PickaxeItem);
		if (!BlockBreaker.breakOneBlock(city))
			city = null;
	}

	private void preventPlacingBack()
	{
		ArrayList<BlockPos> surroundingBlocks = new ArrayList<>();
		surroundingBlocks.add(new BlockPos(city.add(1, -1, 0)));
		surroundingBlocks.add(new BlockPos(city.add(1, -1, 1)));
		surroundingBlocks.add(new BlockPos(city.add(0, -1, 1)));
		surroundingBlocks.add(new BlockPos(city.add(-1, -1, 1)));
		surroundingBlocks.add(new BlockPos(city.add(-1, -1, 0)));
		surroundingBlocks.add(new BlockPos(city.add(-1, -1, -1)));
		surroundingBlocks.add(new BlockPos(city.add(0, -1, -1)));
		surroundingBlocks.add(new BlockPos(city.add(1, -1, -1)));

		BlockPos blockToPlaceCrystal = surroundingBlocks.parallelStream()
				.filter(pos -> !BlockUtils.hasBlock(pos) || BlockUtils.isBlock(Blocks.OBSIDIAN, pos) || BlockUtils.isBlock(Blocks.BEDROCK, pos))
				.filter(this::canPlaceCrystalAssumeIsObby)
				.max(Comparator.comparingDouble(pos -> DamageUtils.crystalDamage((PlayerEntity) target, Vec3d.ofBottomCenter(pos).add(0, 1, 0)))).orElse(null);
		if (blockToPlaceCrystal == null)
			return;
		if (BlockUtils.hasBlock(blockToPlaceCrystal))
		{
			if (InventoryUtils.selectItemFromHotbar(item -> item == Items.END_CRYSTAL))
				CWHACK.getFeatures().crystalAuraFeature.placeCrystal(blockToPlaceCrystal);
		}
		else
		{
			if (InventoryUtils.selectItemFromHotbar(item -> item == Items.OBSIDIAN))
			{
				((IClientPlayerInteractionManager) MC.interactionManager).cwSyncSelectedSlot();
				if (!BlockPlacer.placeBlock(blockToPlaceCrystal))
					BlockPlacer.tryAirPlaceBlock(blockToPlaceCrystal);
				if (InventoryUtils.selectItemFromHotbar(item -> item == Items.END_CRYSTAL))
					CWHACK.getFeatures().crystalAuraFeature.placeCrystal(blockToPlaceCrystal);
			}
		}
	}

	private boolean canPlaceCrystal(BlockPos block)
	{
		BlockState blockState = MC.world.getBlockState(block);
		if (!blockState.isOf(Blocks.OBSIDIAN) && !blockState.isOf(Blocks.BEDROCK))
			return false;
		return canPlaceCrystalAssumeIsObby(block);
	}

	private boolean canPlaceCrystalAssumeIsObby(BlockPos block)
	{
		if (!BlockUtils.isBlockReachable(block, range.getValue()))
			return false;
		BlockPos blockPos2 = block.up();
		if (!MC.world.isAir(blockPos2))
			return false;
		double d = blockPos2.getX();
		double e = blockPos2.getY();
		double f = blockPos2.getZ();
		List<Entity> list = MC.world.getOtherEntities((Entity)null, new Box(d, e, f, d + 1.0D, e + 2.0D, f + 1.0D));
		if (!list.isEmpty())
			return false;
		return true;
	}

	public boolean isCitying()
	{
		if (!isEnabled())
			return false;
		return city != null;
	}

	private boolean shouldCity()
	{
		if (BlockUtils.hasBlock(target.getBlockPos()))
			return true;
		if (HoleUtils.isFullySurrounded(target.getBlockPos()))
			return false;
		return HoleUtils.isSurrounded(target.getBlockPos());
	}

	private Entity findTarget()
	{
		return StreamSupport.stream(MC.world.getEntities().spliterator(), false)
				.filter(e -> e != MC.player)
				.filter(e -> !e.isRemoved())
				.filter(e -> e instanceof PlayerEntity)
				.filter(e -> ((PlayerEntity) e).getHealth() > 0.0f)
				//.filter(e -> MC.player.squaredDistanceTo(e) <= range.getValue() * range.getValue())
				.min(Comparator.comparingDouble(e -> MC.player.squaredDistanceTo(e))).orElse(null);
	}

	private BlockPos findCity()
	{
		BlockPos targetPos = target.getBlockPos();
		if (BlockUtils.getBlock(targetPos) instanceof AnvilBlock
				&& BlockUtils.isBlockReachable(targetPos, range.getValue()))
			return targetPos;
		return BlockUtils.getAllInBoxStream(targetPos.add(-2, 0, -2), targetPos.add(2, 3, 2))
				.filter(BlockUtils::hasBlock)
				.filter(pos -> !BlockUtils.isBlock(Blocks.BEDROCK, pos))
				.filter(pos -> BlockUtils.isBlockReachable(pos, range.getValue()))
				.filter(pos -> !BlockUtils.hasBlock(pos.add(targetPos.subtract(pos))))
				.filter(this::shouldKeepCity) // idk why but we need this
				.min(Comparator.comparingDouble(pos -> Vec3d.of(pos).squaredDistanceTo(Vec3d.of(target.getBlockPos())))).orElse(null);
	}

	private boolean shouldKeepCity(BlockPos pos)
	{
		return BlockUtils.hasBlock(pos) && target.squaredDistanceTo(Vec3d.of(pos)) <= 4.0f && BlockUtils.isBlockReachable(pos, range.getValue());
	}

	@Override
	public void onRender(RenderEvent event)
	{
		if (city == null)
			return;

		MatrixStack matrixStack = event.getMatrixStack();

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		matrixStack.push();
		RenderUtils.applyRegionalRenderOffset(matrixStack);
		BlockPos blockPos = RenderUtils.getCameraBlockPos();
		int regionX = (blockPos.getX() >> 9) * 512;
		int regionZ = (blockPos.getZ() >> 9) * 512;

		matrixStack.translate(city.getX() - regionX,
				city.getY(), city.getZ() - regionZ);

		RenderSystem.setShader(GameRenderer::getPositionShader);

		RenderSystem.setShaderColor(0.4f, 1.0f, 0.4f, 1.0f);
		RenderUtils.drawOutlinedBox(new Box(BlockPos.ORIGIN), matrixStack);

		matrixStack.pop();

		RenderSystem.setShaderColor(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
}
