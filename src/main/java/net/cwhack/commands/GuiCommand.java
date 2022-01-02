package net.cwhack.commands;

import net.cwhack.command.Command;
import net.cwhack.command.exception.CommandException;
import net.cwhack.command.exception.CommandSyntaxException;
import net.cwhack.gui.screen.GuiScreen;

import static net.cwhack.CwHack.MC;

public class GuiCommand extends Command
{

	public GuiCommand()
	{
		super("gui", "open up gui", new String[] {});
	}

	@Override
	public void execute(String[] command) throws CommandException
	{
		if (command.length != 0)
			throw new CommandSyntaxException("argument number not matching");
		MC.setScreen(new GuiScreen());
	}
}
