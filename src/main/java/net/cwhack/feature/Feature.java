package net.cwhack.feature;

import net.cwhack.CwHack;
import net.cwhack.event.EventManager;
import net.cwhack.gui.screen.FeatureSettingScreen;
import net.cwhack.setting.Setting;
import net.cwhack.utils.NotificationUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import static net.cwhack.CwHack.MC;

public abstract class Feature
{
	private final String name;
	private final String description;
	private boolean enabled = false;
	protected EventManager eventManager = CwHack.CWHACK.getEventManager();

	private final HashMap<String, Setting<?>> settings;

	public Feature(String name, String description)
	{
		this.name = name;
		this.description = description;
		settings = new HashMap<>();
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		if (this.enabled == enabled)
			return;

		this.enabled = enabled;

		if (enabled)
		{
			NotificationUtils.notify(name + " has been enabled");
			onEnable();
		}
		else
		{
			NotificationUtils.notify(name + " has been disabled");
			onDisable();
		}
	}

	public String getName()
	{
		return name;
	}

	public String getDescription()
	{
		return description;
	}

	public Setting<?> getSetting(String name)
	{
		return settings.get(name);
	}

	public Set<String> getSettingNames()
	{
		return settings.keySet();
	}

	public Collection<Setting<?>> getSettings()
	{
		return settings.values();
	}

	public void toggle()
	{
		setEnabled(!enabled);
	}

	public FeatureSettingScreen getSettingScreen()
	{
		return new FeatureSettingScreen(MC.currentScreen, this);
	}

	public void addSetting(Setting<?> setting)
	{
		settings.put(setting.getName().toLowerCase(), setting);
	}

	protected void onEnable()
	{

	}

	protected void onDisable()
	{

	}

	public void onChangeSetting(Setting<?> setting)
	{

	}
}
