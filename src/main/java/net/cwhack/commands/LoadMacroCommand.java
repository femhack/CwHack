package net.cwhack.commands;

import net.cwhack.command.Command;
import net.cwhack.command.exception.CommandException;
import net.cwhack.command.exception.CommandInvalidArgumentException;
import net.cwhack.command.exception.CommandMacroException;

import static net.cwhack.CwHack.CWHACK;

public class LoadMacroCommand extends Command
{

	public LoadMacroCommand()
	{
		super("loadmacro", "load and compile a macro", new String[]{"macro name"});
	}

	@Override
	public void execute(String[] command) throws CommandException
	{
		if (command.length != 1)
			throw new CommandInvalidArgumentException("argument number not matching");
		try
		{
			CWHACK.getMacroManager().loadMacro(command[0]);
		} catch (Exception e)
		{
			throw new CommandMacroException("failed to load the macro");
		}
	}
}
