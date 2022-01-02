package net.cwhack.command.exception;

import net.cwhack.utils.ChatUtils;

public class CommandMacroException extends CommandException
{

	public CommandMacroException(String message)
	{
		super(message);
	}

	@Override
	public void printToChat()
	{
		ChatUtils.error("Command Macro Error: " + getMessage());
	}
}
