package net.cwhack.command.exception;

import net.cwhack.utils.ChatUtils;

public class CommandBadConfigException extends CommandException
{
	public CommandBadConfigException(String configName)
	{
		super(configName);
	}

	@Override
	public void printToChat()
	{
		ChatUtils.error("Command Bad Config Error: Config " + getMessage() + " may have corrupted / not found / outdated");
	}
}
