package net.cwhack.commands;

import net.cwhack.command.Command;
import net.cwhack.command.exception.CommandException;
import net.cwhack.command.exception.CommandInvalidArgumentException;
import net.cwhack.command.exception.CommandSyntaxException;
import net.cwhack.feature.Feature;
import net.cwhack.setting.Setting;
import net.cwhack.utils.ChatUtils;

import static net.cwhack.CwHack.CWHACK;

public class ListSettingsCommand extends Command
{

	public ListSettingsCommand()
	{
		super("ListSettings", "List all the settings of a feature", new String[]{"feature name"});
	}

	@Override
	public void execute(String[] command) throws CommandException
	{
		if (command.length != 1)
			throw new CommandSyntaxException("argument number not matching");
		Feature feature = CWHACK.getFeatures().getFeature(command[0]);
		if (feature == null)
			throw new CommandInvalidArgumentException("no feature named " + command[0]);
		for (Setting setting : feature.getSettings())
		{
			ChatUtils.info(setting.getName() + ": " + setting.storeAsString());
		}
	}
}
