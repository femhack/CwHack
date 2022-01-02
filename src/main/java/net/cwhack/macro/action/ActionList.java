package net.cwhack.macro.action;

import net.cwhack.macro.actions.ClickAction;
import net.cwhack.macro.actions.JumpAction;
import net.cwhack.macro.actions.PressKeyAction;
import net.cwhack.macro.actions.SleepAction;

import java.util.HashMap;

public class ActionList
{

	private final HashMap<String, Class<? extends Action>> actions = new HashMap<>();

	public ActionList()
	{
		actions.put("click", ClickAction.class);
		actions.put("jump", JumpAction.class);
		actions.put("press", PressKeyAction.class);
		actions.put("sleep", SleepAction.class);
	}

	public Class<? extends Action> getAction(String name)
	{
		return actions.get(name);
	}
}
