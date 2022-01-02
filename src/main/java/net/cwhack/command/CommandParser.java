package net.cwhack.command;

import net.cwhack.CwHack;
import net.cwhack.command.exception.CommandException;
import net.cwhack.events.ChatOutputListener;
import net.cwhack.utils.ChatUtils;

public class CommandParser implements ChatOutputListener
{
	public static final String COMMAND_PREFIX = "@";

	public final CommandList commandList = new CommandList();

	public CommandParser()
	{
		CwHack.CWHACK.getEventManager().add(ChatOutputListener.class, this, Integer.MAX_VALUE);
	}

	@Override
	public void onSendMessage(ChatOutputEvent event)
	{
		String message = event.getOriginalMessage();
		if (!isCommand(message))
			return;

		event.cancel();

		message = message.substring(1);
		execute(message);
	}

	public void execute(String line)
	{
		if (line.length() == 0)
		{
			ChatUtils.error("no command found");
			return;
		}
		String[] args = line.split(" ");

		Command command = commandList.getCommand(args[0].toLowerCase());
		if (command == null)
		{
			ChatUtils.error("no command is named " + args[0]);
			return;
		}

		String[] arguments = new String[args.length - 1];
		System.arraycopy(args, 1, arguments, 0, args.length - 1);

		try
		{
			command.execute(arguments);
		} catch (CommandException e)
		{
			e.printToChat();
		}
	}

	public boolean isCommand(String message)
	{
		return message.startsWith(COMMAND_PREFIX);
	}
}
