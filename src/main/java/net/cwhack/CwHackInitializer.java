package net.cwhack;

import net.fabricmc.api.ModInitializer;

public class CwHackInitializer implements ModInitializer
{
	@Override
	public void onInitialize()
	{
		CwHack.CWHACK.init();
	}
}
