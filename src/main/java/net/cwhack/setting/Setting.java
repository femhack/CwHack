package net.cwhack.setting;

public abstract class Setting<T>
{
	private final String name;
	private final String description;

	public Setting(String name, String description)
	{
		this.name = name;
		this.description = description;
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

	public abstract void loadFromString(String string);

	public abstract String storeAsString();
}
