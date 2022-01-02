package net.cwhack.macro;

import net.cwhack.macro.action.Action;
import net.cwhack.macro.action.ActionList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import static net.cwhack.CwHack.CWHACK;

public class MacroManager
{

	private final HashMap<String, Macro> loadedMacros = new HashMap<>();

	private final ActionList actionList = new ActionList();

	public static Path getMacroDir()
	{
		return CWHACK.getCwHackDirectory().resolve("macro");
	}

	public MacroManager()
	{
		Path macroDir = getMacroDir();
		try
		{
			Files.createDirectories(macroDir);
		} catch (IOException ignored)
		{
		}
	}

	public void loadMacro(String name)
	{
		Path macroDir = getMacroDir();
		Path macroFilePath = macroDir.resolve(name + ".cwmacro");
		ArrayList<Action> actions = new ArrayList<>();
		try
		{
			if (Files.notExists(macroFilePath))
				throw new RuntimeException();
			Scanner scanner = new Scanner(macroFilePath);
			while (scanner.hasNextLine())
			{
				String line = scanner.nextLine().toLowerCase();
				if (line.length() == 0)
					continue;
				String[] args = line.split(" ");

				Class<? extends Action> actionClass = actionList.getAction(args[0]);
				if (actionClass == null)
					throw new RuntimeException();
				Action action = actionClass.getConstructor().newInstance();
				String[] arguments = new String[args.length - 1];
				System.arraycopy(args, 1, arguments, 0, arguments.length);
				action.init(arguments);

				actions.add(action);
			}
		} catch (Exception e)
		{
			throw new RuntimeException();
		}
		loadedMacros.put(name, new Macro(actions));
	}

	public void runMacro(String name)
	{
		Macro macro = loadedMacros.get(name);
		if (macro == null)
			throw new RuntimeException();
		(new Thread(macro)).start();
	}

	public List<Macro> getLoadedMacros()
	{
		return loadedMacros.values().stream().toList();
	}
}
