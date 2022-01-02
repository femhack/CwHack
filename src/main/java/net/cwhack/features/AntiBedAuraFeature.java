package net.cwhack.features;

import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;

public class AntiBedAuraFeature extends Feature implements UpdateListener
{
	public AntiBedAuraFeature()
	{
		super("AntiBedAura", "prevent others from placing bed on you, only works in 1x1 holes");
	}

	@Override
	public void onEnable()
	{
		eventManager.add(UpdateListener.class, this);
	}

	@Override
	public void onDisable()
	{
		eventManager.remove(UpdateListener.class, this);
	}

	@Override
	public void onUpdate()
	{

	}
}
