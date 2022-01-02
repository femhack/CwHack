package net.cwhack.setting;

public class IntegerSetting extends Setting<Integer>
{

	private int value;

	public IntegerSetting(String name, String description, int value)
	{
		super(name, description);
		this.value = value;
	}

	@Override
	public void loadFromString(String string)
	{
		value = Integer.parseInt(string);
	}

	@Override
	public String storeAsString()
	{
		return Integer.toString(value);
	}

	@Override
	public Integer getValue()
	{
		return value;
	}
}
