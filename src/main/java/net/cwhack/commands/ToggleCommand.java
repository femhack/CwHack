package net.cwhack.commands;

import net.cwhack.CwHack;
import net.cwhack.command.Command;
import net.cwhack.command.exception.CommandException;
import net.cwhack.command.exception.CommandInvalidArgumentException;
import net.cwhack.command.exception.CommandSyntaxException;
import net.cwhack.feature.Feature;
import net.cwhack.feature.FeatureList;

public class ToggleCommand extends Command
{
	public ToggleCommand()
	{
		super("toggle", "Toggle a feature", new String[]{"feature"});
	}

	@Override
	public void execute(String[] command) throws CommandException
	{
		if (command.length != 1)
			throw new CommandSyntaxException("argument number not matching");
		FeatureList featureList = CwHack.CWHACK.getFeatures();
		Feature feature2Toggle = featureList.getFeature(command[0]);
		if (feature2Toggle == null)
			throw new CommandInvalidArgumentException("no feature named " + command[0]);
		feature2Toggle.toggle();
	}
}
