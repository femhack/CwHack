package net.cwhack.commands;

import net.cwhack.command.Command;
import net.cwhack.command.exception.CommandException;
import net.cwhack.command.exception.CommandSyntaxException;
import net.cwhack.utils.ChatUtils;

import java.nio.file.Path;

import static net.cwhack.CwHack.CWHACK;

public class SaveCommand extends Command
{

	public SaveCommand()
	{
		super("save", "save a configuration", new String[]{"config name"});
	}

	@Override
	public void execute(String[] command) throws CommandException
	{
		if (command.length != 1)
			throw new CommandSyntaxException("argument number not matching");
		writeConfig(command[0]);

		ChatUtils.info("config saved");
	}

	private void writeConfig(String name)
	{
		Path configDir = CWHACK.getConfigDirectory();
		Path configFilePath = configDir.resolve(name + ".cw");
		CWHACK.getFeatures().saveAsFile(configFilePath.toString());
	}
}
