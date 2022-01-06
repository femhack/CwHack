package net.cwhack.setting;

import net.cwhack.feature.Feature;

public abstract class Setting<T>
{
	private final String name;
	private final String description;
	private final Feature feature;

	public Setting(String name, String description, Feature feature)
	{
		this.name = name;
		this.description = description;
		this.feature = feature;
	}

	public String getName()
	{
		return name;
	}

	public String getDescription()
	{
		return description;
	}

	public abstract T getValue();

	public void loadFromString(String string)
	{
		loadFromStringInternal(string);
		feature.onChangeSetting(this);
	}

	protected abstract void loadFromStringInternal(String string);

	public abstract String storeAsString();
}
