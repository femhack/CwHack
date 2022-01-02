package net.cwhack.commands;

import net.cwhack.command.Command;
import net.cwhack.command.exception.CommandException;
import net.cwhack.command.exception.CommandInvalidArgumentException;
import net.cwhack.command.exception.CommandSyntaxException;
import net.cwhack.utils.ChatUtils;

import java.util.Set;

import static net.cwhack.CwHack.CWHACK;

public class HelpCommand extends Command
{

	public HelpCommand()
	{
		super("help", "check usage and syntax for a specific command, when the command is not specified, all available command will be listed", new String[]{"command (optional)"});
	}

	@Override
	public void execute(String[] command) throws CommandException
	{
		if (command.length > 1)
			throw new CommandSyntaxException("argument number not matching");
		if (command.length == 0)
		{
			ChatUtils.info("All available commands:");
			listAllCommands();
			ChatUtils.info("do @help [command] to see the usage and syntax for a specific command");
		}
		if (command.length == 1)
		{
			Command cmd = CWHACK.getCommandParser().commandList.getCommand(command[0]);
			if (cmd == null)
				throw new CommandInvalidArgumentException("no command named " + command[0] + " found");
			ChatUtils.info(cmd.getDescription());
			StringBuilder syntax = new StringBuilder("Syntax: " + cmd.getName());
			for (String string : cmd.getSyntax())
			{
				syntax.append(" [").append(string).append("]");
			}
			ChatUtils.info(syntax.toString());
		}
	}

	private void listAllCommands()
	{
		Set<String> commands = CWHACK.getCommandParser().commandList.getAllCommandNames();
		for (String command : commands)
		{
			ChatUtils.info(command);
		}
	}
}
