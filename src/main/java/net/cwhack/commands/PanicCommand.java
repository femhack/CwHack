package net.cwhack.commands;

import net.cwhack.command.Command;
import net.cwhack.command.exception.CommandException;
import net.cwhack.command.exception.CommandSyntaxException;

import static net.cwhack.CwHack.CWHACK;

public class PanicCommand extends Command
{
	public PanicCommand()
	{
		super("panic", "Turn off all features and unbind all keybinds", new String[]{});
	}

	@Override
	public void execute(String[] command) throws CommandException
	{
		if (command.length != 0)
			throw new CommandSyntaxException("argument number not matching");
		CWHACK.getKeybindManager().removeAll();
		CWHACK.getFeatures().turnOffAll();
	}
}
