package net.cwhack.macro.action;

import net.cwhack.macro.exception.MacroException;

public abstract class Action
{
	public abstract void init(String[] args) throws MacroException;

	public abstract void run();
}
