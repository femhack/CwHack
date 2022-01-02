package net.cwhack.macro;

import net.cwhack.macro.action.Action;

import java.util.ArrayList;

public class Macro implements Runnable
{

	private ArrayList<Action> actions;

	public Macro(ArrayList<Action> actions)
	{
		this.actions = new ArrayList<>(actions);
	}

	@Override
	public void run()
	{
		actions.forEach(Action::run);
	}
}
