package net.cwhack.utils;

import net.minecraft.text.LiteralText;
import org.apache.logging.log4j.LogManager;

import static net.cwhack.CwHack.CWHACK;
import static net.cwhack.CwHack.MC;

public enum ChatUtils
{
	;
	private static final String prefix = "§f[§9CwHack§f] ";

	public static void log(String message)
	{
		if (CWHACK.isGhostMode())
			return;
		LogManager.getLogger().info("[CWHACK] {}", message.replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
	}
	public static void info(String message)
	{
		if (CWHACK.isGhostMode())
			return;
		String string = prefix + "Info: " + message;
		sendPlainMessage(string);
	}
	public static void error(String message)
	{
		if (CWHACK.isGhostMode())
			return;
		String string = prefix + "§4Error: §f" + message;
		sendPlainMessage(string);
		log(message);
	}
	public static void sendPlainMessage(String message)
	{
		if (CWHACK.isGhostMode())
			return;
		if (MC.inGameHud != null)
			MC.inGameHud.getChatHud().addMessage(new LiteralText(message));
	}
}
