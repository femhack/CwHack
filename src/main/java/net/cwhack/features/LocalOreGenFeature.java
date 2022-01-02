package net.cwhack.features;

import net.cwhack.events.RenderListener;
import net.cwhack.feature.Feature;
import net.cwhack.setting.TextSetting;

public class LocalOreGenFeature extends Feature implements RenderListener
{

	private final TextSetting seed = new TextSetting("seed", "the seed used for ore generation", "");

	public LocalOreGenFeature()
	{
		super("LocalOreGen", "Generate ores on client side and show them on screen. Can be used to bypass anti xray");
	}

	@Override
	protected void onEnable()
	{
		super.onEnable();
	}

	@Override
	protected void onDisable()
	{
		super.onDisable();
	}

	@Override
	public void onRender(RenderEvent event)
	{

	}
}
