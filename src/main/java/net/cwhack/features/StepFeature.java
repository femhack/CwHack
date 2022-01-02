package net.cwhack.features;

import net.cwhack.CwHack;
import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;

public class StepFeature extends Feature implements UpdateListener
{

	public StepFeature()
	{
		super("Step", "allow you to step up blocks");
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
		CwHack.MC.player.stepHeight = 0.5f;
	}

	@Override
	public void onUpdate()
	{
		CwHack.MC.player.stepHeight = 1.0f;
	}
}
