package net.cwhack.features;

import net.cwhack.events.FrameBeginListener;
import net.cwhack.feature.Feature;
import net.cwhack.setting.BooleanSetting;
import net.cwhack.setting.DecimalSetting;
import net.minecraft.item.Items;
import net.minecraft.util.Util;

import java.util.Random;

import static net.cwhack.CwHack.MC;

public class AutoHeadBobFeature extends Feature implements FrameBeginListener
{

	private final DecimalSetting amplitude = new DecimalSetting("amplitude", "how much you want to bob your head", 0.25, this);
	private final DecimalSetting frequency = new DecimalSetting("frequency", "how fast you want to bob your head", 5.0, this);
	private final BooleanSetting verticalRandomness = new BooleanSetting("verticalRandomness", "whether or not to add random vertical offset", false, this);
	private final BooleanSetting horizontalRandomness = new BooleanSetting("horizontalRandomness", "whether or not to add random horizontal offset", false, this);
	private final DecimalSetting randomness = new DecimalSetting("randomness", "how random", 0.2, this);

	private final Random rng = new Random();

	public AutoHeadBobFeature()
	{
		super("AutoHeadBob", "bob your head like ceeew when crystalling, useful on high ping");
	}

	private long startTime = 0;
	private long lastFrame = 0;

	@Override
	public void onEnable()
	{
		startTime = Util.getMeasuringTimeMs();
		lastFrame = startTime;
		eventManager.add(FrameBeginListener.class, this);
	}

	@Override
	public void onDisable()
	{
		eventManager.remove(FrameBeginListener.class, this);
	}

	@Override
	public void onFrameBegin()
	{
		if (MC.player == null)
			return;
		if (!MC.player.getMainHandStack().isOf(Items.END_CRYSTAL))
			return;

		float tickDelta = MC.getTickDelta();
		long currentTime = Util.getMeasuringTimeMs();
		long delta = currentTime - lastFrame;
		lastFrame = currentTime;

		// I want the offset from the starting point of the head bob to be a sin wave,
		// so in the beginning of every frame I will calculate the derivative of the wave and add that to the pitch
		long timePast = currentTime - startTime;
		double f = timePast / 1000.0 * 2.0 * Math.PI * frequency.getValue();
		double g = -Math.cos(f) * amplitude.getValue(); // derivative of sin is cosine
		double h = 0.0;

		// random offsets
		double random = rng.nextDouble();
		// mapping [0, 1] to [-randomness, randomness]
		double randomnessV = randomness.getValue();
		random = random * 2 * randomnessV - randomnessV;

		if (verticalRandomness.getValue())
			g += random;
		if (horizontalRandomness.getValue())
			h += random;

		MC.player.changeLookDirection(h, g);

//		float pitch = MC.player.getPitch();
//		float yaw = MC.player.getYaw();
//		MC.player.setPitch(pitch + (float) g);
//		MC.player.setYaw(yaw + (float) h);
	}
}
