package net.cwhack.commands;

import net.cwhack.CwHack;
import net.cwhack.command.Command;
import net.cwhack.command.exception.CommandException;
import net.cwhack.command.exception.CommandSyntaxException;
import net.cwhack.utils.ChatUtils;

public class UnbindCommand extends Command
{

	public UnbindCommand()
	{
		super("unbind", "Unbind a keybind", new String[]{"name"});
	}

	@Override
	public void execute(String[] command) throws CommandException
	{
		if (command.length != 1)
			throw new CommandSyntaxException("argument number not matching");

		CwHack.CWHACK.getKeybindManager().removeKeybind(command[0]);

		ChatUtils.info("keybind unbinded");
	}
}
