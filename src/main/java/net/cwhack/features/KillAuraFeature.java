package net.cwhack.features;

import net.cwhack.events.PostMotionListener;
import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;
import net.cwhack.setting.DecimalSetting;
import net.cwhack.setting.IntegerSetting;
import net.cwhack.utils.RotationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;

import static net.cwhack.CwHack.CWHACK;
import static net.cwhack.CwHack.MC;

public class KillAuraFeature extends Feature implements UpdateListener, PostMotionListener
{

	private final DecimalSetting range = new DecimalSetting("range", "the range you will attack the player", 3.0, this);
	private final IntegerSetting critical = new IntegerSetting("critical", "whether or not to use critical hits", 1, this);
	private final DecimalSetting cooldown = new DecimalSetting("cooldown", "the required cooldown progress for the next attack", 1.0, this);

	private Entity target;
	private boolean overridingTarget = false;

	private boolean attack;

	public KillAuraFeature()
	{
		super("KillAura", "killaura");
	}

	@Override
	protected void onEnable()
	{
		eventManager.add(UpdateListener.class, this);
		eventManager.add(PostMotionListener.class, this);
		attack = false;
	}

	@Override
	protected void onDisable()
	{
		eventManager.remove(UpdateListener.class, this);
		eventManager.remove(PostMotionListener.class, this);
	}

	private Entity findTarget()
	{
		return MC.world.getPlayers().stream()
				.filter(e -> e != MC.player)
				.filter(LivingEntity::isAlive)
				.filter(e -> e.getHealth() > 0.0f)
				.min(Comparator.comparingDouble(e -> RotationUtils.getAngleToLookVec(e.getBoundingBox().getCenter()))).orElse(null);
	}

	private void doCrit()
	{
		if (MC.player.isTouchingWater() || MC.player.isInLava() || !MC.player.isOnGround())
			return;

		double x = MC.player.getX();
		double y = MC.player.getY();
		double z = MC.player.getZ();

		MC.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.0625, z, false));
		MC.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0, z, false));
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
		if (CWHACK.getRotationFaker().isFaking())
			return;
		if (RotationUtils.getEyesPos().squaredDistanceTo(getAimPos(target)) > range.getValue() * range.getValue())
			return;
		CWHACK.getRotationFaker().setServerLookPos(getAimPos(target));
		if (MC.player.getAttackCooldownProgress(0) < cooldown.getValue())
			return;
		attack = true;
	}

	@Override
	public void onPostMotion()
	{
		if (!attack)
			return;
		attack = false;
		if (critical.getValue() != 0)
			doCrit();
		MC.interactionManager.attackEntity(MC.player, target);
		MC.player.swingHand(Hand.MAIN_HAND);
	}

	private Vec3d getAimPos(Entity entity)
	{
		return entity.getBoundingBox().getCenter();
	}
}
