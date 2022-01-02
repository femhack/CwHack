package net.cwhack.command;

import net.cwhack.commands.*;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Set;

public class CommandList
{
	public final BindCommand bindCommand = new BindCommand();
	public final GuiCommand guiCommand = new GuiCommand();
	public final HelpCommand helpCommand = new HelpCommand();
	public final ListCommand listCommand = new ListCommand();
	public final ListSettingsCommand listSettingsCommand = new ListSettingsCommand();
	public final LoadCommand loadCommand = new LoadCommand();
	public final LoadMacroCommand loadMacroCommand = new LoadMacroCommand();
	public final PanicCommand panicCommand = new PanicCommand();
	public final RunCommand runCommand = new RunCommand();
	public final RunMacroCommand runMacroCommand = new RunMacroCommand();
	public final SaveCommand saveCommand = new SaveCommand();
	public final SetCommand setCommand = new SetCommand();
	public final ToggleCommand toggleCommand = new ToggleCommand();
	public final UnbindCommand unbindCommand = new UnbindCommand();
	private final HashMap<String, Command> commands = new HashMap<>();

	public CommandList()
	{
		try
		{
			for (Field field : CommandList.class.getDeclaredFields())
			{
				if (!field.getName().endsWith("Command"))
					continue;

				Command cmd = (Command) field.get(this);
				commands.put(cmd.getName().toLowerCase(), cmd);
			}
		} catch (Exception e)
		{
			String message = "Initializing CwHack commands";
			CrashReport report = CrashReport.create(e, message);
			throw new CrashException(report);
		}
	}

	public Command getCommand(String name)
	{
		return commands.get(name);
	}

	public Set<String> getAllCommandNames()
	{
		return commands.keySet();
	}
}
