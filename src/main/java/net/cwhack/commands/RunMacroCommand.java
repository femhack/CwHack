package net.cwhack.commands;

import net.cwhack.command.Command;
import net.cwhack.command.exception.CommandException;
import net.cwhack.command.exception.CommandInvalidArgumentException;
import net.cwhack.command.exception.CommandMacroException;

import static net.cwhack.CwHack.CWHACK;

public class RunMacroCommand extends Command
{

	public RunMacroCommand()
	{
		super("runmacro", "run a macro", new String[]{"macro name"});
	}

	@Override
	public void execute(String[] command) throws CommandException
	{
		if (command.length != 1)
			throw new CommandInvalidArgumentException("argument number not matching");
		try
		{
			CWHACK.getMacroManager().runMacro(command[0]);
		} catch (Exception e)
		{
			throw new CommandMacroException("failed to run the macro");
		}
	}
}
