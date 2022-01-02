package net.cwhack.features;

import net.cwhack.events.ChatOutputListener;
import net.cwhack.feature.Feature;

public class CwHackPostfixFeature extends Feature implements ChatOutputListener
{
	public CwHackPostfixFeature()
	{
		super("CwHackPostfix", "Automatically put cw hack postfix after every chat message");
	}

	@Override
	protected void onEnable()
	{
		eventManager.add(ChatOutputListener.class, this);
	}

	@Override
	protected void onDisable()
	{
		eventManager.remove(ChatOutputListener.class, this);
	}

	@Override
	public void onSendMessage(ChatOutputEvent event)
	{
		if (event.getOriginalMessage().startsWith("/"))
			return;
		event.setMessage(event.getOriginalMessage() + " | CwHack on top");
	}
}
