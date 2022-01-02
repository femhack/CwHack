package net.cwhack.features;

import net.cwhack.events.RenderListener;
import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;
import net.cwhack.setting.DecimalSetting;
import net.cwhack.utils.RotationUtils;
import net.cwhack.utils.RotationUtils.Rotation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.stream.StreamSupport;

import static net.cwhack.CwHack.MC;

public class AimAssistFeature extends Feature implements UpdateListener, RenderListener
{

	private DecimalSetting range = new DecimalSetting("range", "the range the aimbot will work", 4);
	private DecimalSetting speed = new DecimalSetting("speed", "the speed your head will turn", 1);

	private Entity target;

	public AimAssistFeature()
	{
		super("AimAssist", "automatically aims for you");
		addSetting(range);
		addSetting(speed);
	}

	@Override
	public void onEnable()
	{
		eventManager.add(UpdateListener.class, this);
		eventManager.add(RenderListener.class, this);
	}

	@Override
	public void onDisable()
	{
		eventManager.remove(UpdateListener.class, this);
		eventManager.remove(RenderListener.class, this);
	}

	@Override
	public void onUpdate()
	{
		target = findTarget();
	}

	@Override
	public void onRender(RenderEvent event)
	{
		if (target == null)
			return;

		float delta = event.getPartialTicks();
		Vec3d targetDelta = target.getPos().subtract(target.prevX, target.prevY, target.prevZ);
		Vec3d aimAt = target.getBoundingBox().getCenter().add(0.0, 0.5, 0.0).add(targetDelta.multiply(delta));
		Vec3d playerDelta = MC.player.getPos().subtract(MC.player.prevX, MC.player.prevY, MC.player.prevZ);
		Rotation rotation = RotationUtils.getNeededRotations(RotationUtils.getEyesPos().add(playerDelta.multiply(delta)), aimAt);

		if (RotationUtils.getEyesPos().squaredDistanceTo(aimAt) > range.getValue() * range.getValue())
			return;

		float playerYaw = MC.player.getYaw();
		float playerPitch = MC.player.getPitch();

		double deltaAngle;
		double toRotate;

		deltaAngle = MathHelper.wrapDegrees(rotation.getYaw() - playerYaw);
		//toRotate = speed.getValue() * (deltaAngle >= 0 ? 1 : -1) * delta;
		toRotate = speed.getValue() * deltaAngle * delta;
		if ((toRotate >= 0 && toRotate > deltaAngle) || (toRotate < 0 && toRotate < deltaAngle)) toRotate = deltaAngle;
		MC.player.setYaw(playerYaw + (float) toRotate);

		deltaAngle = MathHelper.wrapDegrees(rotation.getPitch() - playerPitch);
		//toRotate = speed.getValue() * (deltaAngle >= 0 ? 1 : -1) * delta;
		toRotate = speed.getValue() * deltaAngle * delta;
		if ((toRotate >= 0 && toRotate > deltaAngle) || (toRotate < 0 && toRotate < deltaAngle)) toRotate = deltaAngle;
		MC.player.setPitch(playerPitch + (float) toRotate);
	}

	private Entity findTarget()
	{
		return StreamSupport.stream(MC.world.getEntities().spliterator(), true)
				.filter(e -> e instanceof PlayerEntity)
				.filter(e -> e != MC.player)
				.filter(e -> !e.isRemoved())
				.min(Comparator.comparingDouble(e -> RotationUtils.getAngleToLookVec(e.getBoundingBox().getCenter().add(0.0, 0.5, 0.0))))
				.orElse(null);
	}
}
