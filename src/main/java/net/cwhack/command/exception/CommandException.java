package net.cwhack.command.exception;

public abstract class CommandException extends Exception
{
	public CommandException(String message)
	{
		super(message);
	}

	public abstract void printToChat();
}
