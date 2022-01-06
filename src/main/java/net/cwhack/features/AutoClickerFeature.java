package net.cwhack.features;

import net.cwhack.feature.Feature;
import net.cwhack.mixinterface.IMouse;
import net.cwhack.setting.BooleanSetting;
import net.cwhack.setting.IntegerSetting;
import net.cwhack.setting.Setting;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static net.cwhack.CwHack.MC;

public class AutoClickerFeature extends Feature
{

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private ScheduledFuture<?> clickerHandle;

	private final IntegerSetting interval = new IntegerSetting("interval", "the interval of the auto clicker (in milliseconds)", 1000, this);
	private final BooleanSetting pauseInGui = new BooleanSetting("pauseInGui", "whether or not the auto clicker should pause in gui", true, this);

	public AutoClickerFeature()
	{
		super("AutoClicker", "a built-in left click auto clicker");
		addSetting(interval);
		addSetting(pauseInGui);
	}

	@Override
	protected void onEnable()
	{
		clickerHandle = scheduler.scheduleAtFixedRate(new Clicker(pauseInGui.getValue()), 0, interval.getValue(), MILLISECONDS);
	}

	@Override
	protected void onDisable()
	{
		clickerHandle.cancel(false);
	}

	@Override
	public void onChangeSetting(Setting setting)
	{
		if (setting == interval && isEnabled())
		{
			onDisable();
			onEnable();
		}
	}

	private record Clicker(boolean pauseInGui) implements Runnable
	{

		@Override
		public void run()
		{
			click();
		}

		private void click()
		{
			MC.execute(() ->
			{
				if (MC.currentScreen != null && pauseInGui)
					return;
				IMouse mouse = (IMouse) MC.mouse;
				mouse.cwOnMouseButton(MC.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_1, GLFW.GLFW_PRESS, 0);
				mouse.cwOnMouseButton(MC.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_1, GLFW.GLFW_RELEASE, 0);
			});
		}
	}
}
