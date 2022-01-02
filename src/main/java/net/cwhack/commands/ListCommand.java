package net.cwhack.commands;

import net.cwhack.command.Command;
import net.cwhack.command.exception.CommandException;
import net.cwhack.command.exception.CommandSyntaxException;
import net.cwhack.utils.ChatUtils;

import static net.cwhack.CwHack.CWHACK;

public class ListCommand extends Command
{

	public ListCommand()
	{
		super("list", "List all available features", new String[]{});
	}

	@Override
	public void execute(String[] command) throws CommandException
	{
		if (command.length != 0)
			throw new CommandSyntaxException("argument number not matching");
		for (String feature : CWHACK.getFeatures().getAllFeatureNames())
		{
			ChatUtils.info(CWHACK.getFeatures().getFeature(feature).getName());
		}
	}
}
