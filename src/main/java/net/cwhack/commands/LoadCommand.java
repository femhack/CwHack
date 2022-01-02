package net.cwhack.commands;

import net.cwhack.command.Command;
import net.cwhack.command.exception.CommandBadConfigException;
import net.cwhack.command.exception.CommandException;
import net.cwhack.command.exception.CommandSyntaxException;
import net.cwhack.utils.ChatUtils;

import java.nio.file.Path;

import static net.cwhack.CwHack.CWHACK;

public class LoadCommand extends Command
{

	public LoadCommand()
	{
		super("load", "load a configuration", new String[]{"config name"});
	}

	@Override
	public void execute(String[] command) throws CommandException
	{
		if (command.length != 1)
			throw new CommandSyntaxException("argument number not matching");
		Path configDir = CWHACK.getCwHackDirectory().resolve("config");
		Path configFilePath = configDir.resolve(command[0] + ".cw");

		try
		{
			CWHACK.getFeatures().loadFromFile(configFilePath.toString());
		}
		catch (Exception e)
		{
			throw new CommandBadConfigException(command[0]);
		}

		ChatUtils.info("config loaded");
	}
}
