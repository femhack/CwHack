package net.cwhack;

import net.cwhack.command.CommandList;
import net.cwhack.command.CommandParser;
import net.cwhack.event.EventManager;
import net.cwhack.feature.FeatureList;
import net.cwhack.gui.Gui;
import net.cwhack.keybind.KeybindManager;
import net.cwhack.macro.MacroManager;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public enum CwHack
{
	CWHACK;

	public static final MinecraftClient MC = MinecraftClient.getInstance();

	private Path cwHackDirectory;

	private EventManager eventManager;

	private FeatureList featureList;

	private CommandParser commandParser;
	private CommandList commandList;

	private CrystalDataTracker crystalDataTracker;
	private RotationFaker rotationFaker;

	private KeybindManager keybindManager;

	private FriendList friendList;

	private Gui gui;

	private NotificationRenderer notificationRenderer;

	private MacroManager macroManager;

	private boolean ghostMode;

	public void init()
	{
		cwHackDirectory = createCwHackDirectory();

		eventManager = new EventManager();

		featureList = new FeatureList();

		commandParser = new CommandParser();
		commandList = new CommandList();

		crystalDataTracker = new CrystalDataTracker();
		rotationFaker = new RotationFaker();

		keybindManager = new KeybindManager();

		friendList = new FriendList();

		gui = new Gui();

		notificationRenderer = new NotificationRenderer();

		macroManager = new MacroManager();

		Path isGhostFilePath = cwHackDirectory.resolve("GHOST");

		ghostMode = false;

		if (Files.notExists(isGhostFilePath))
		{
			try
			{
				Files.createFile(isGhostFilePath);
			} catch (IOException e)
			{
				throw new RuntimeException("Failed to create GHOST file");
			}
			return;
		}

		try
		{
			Scanner scanner = new Scanner(isGhostFilePath);
			if (scanner.hasNextLine())
				if (scanner.nextLine().equalsIgnoreCase("true"))
					ghostMode = true;
		} catch (IOException e)
		{
			throw new RuntimeException("Failed to open GHOST file");
		}
	}

	public Path getCwHackDirectory()
	{
		return cwHackDirectory;
	}

	public EventManager getEventManager()
	{
		return eventManager;
	}

	public FeatureList getFeatures()
	{
		return featureList;
	}

	public CommandParser getCommandParser()
	{
		return commandParser;
	}

	public CrystalDataTracker getCrystalDataTracker()
	{
		return crystalDataTracker;
	}

	public RotationFaker getRotationFaker()
	{
		return rotationFaker;
	}

	public KeybindManager getKeybindManager()
	{
		return keybindManager;
	}

	public FriendList getFriendList()
	{
		return friendList;
	}

	public Gui getGui()
	{
		return gui;
	}

	public NotificationRenderer getNotificationRenderer()
	{
		return notificationRenderer;
	}

	public MacroManager getMacroManager()
	{
		return macroManager;
	}

	public boolean isGhostMode()
	{
		return ghostMode;
	}

	public Path getConfigDirectory()
	{
		return CWHACK.getCwHackDirectory().resolve("config");
	}

	public Path getScriptDirectory()
	{
		return CWHACK.getCwHackDirectory().resolve("script");
	}

	private Path createCwHackDirectory()
	{
		//Path mcDir = MC.runDirectory.toPath().normalize();
		//Path cwHackDirectory = mcDir.resolve("cwhack");
		Path userDir = Path.of(System.getProperty("user.home"));
		Path cwHackDirectory = userDir.resolve(".cwhack");
		try
		{
			Files.createDirectories(cwHackDirectory);
			if (System.getProperty("os.name").toLowerCase().contains("windows"))
			{
				Files.setAttribute(cwHackDirectory, "dos:hidden", true);
			}
		} catch (IOException e)
		{
			throw new RuntimeException("Failed to create cwhack folder");
		}

		return cwHackDirectory;
	}
}
