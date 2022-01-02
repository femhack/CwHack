package net.cwhack.setting;

public class BooleanSetting extends Setting<Boolean>
{
	private boolean value;

	public BooleanSetting(String name, String description, boolean value)
	{
		super(name, description);
		this.value = value;
	}

	@Override
	public Boolean getValue()
	{
		return value;
	}

	@Override
	public void loadFromString(String string)
	{
		value = Boolean.parseBoolean(string);
	}

	@Override
	public String storeAsString()
	{
		return Boolean.toString(value);
	}
}
