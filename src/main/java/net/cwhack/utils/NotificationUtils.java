package net.cwhack.utils;

import static net.cwhack.CwHack.CWHACK;

public enum NotificationUtils
{
	;
	public static void notify(String string)
	{
		if (!CWHACK.isGhostMode())
			CWHACK.getNotificationRenderer().sendNotification(string);
	}
}
