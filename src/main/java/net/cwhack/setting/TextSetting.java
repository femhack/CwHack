package net.cwhack.setting;

public class TextSetting extends Setting<String>
{

	private String value;

	public TextSetting(String name, String description, String value)
	{
		super(name, description);
		this.value = value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	@Override
	public String getValue()
	{
		return value;
	}

	@Override
	public void loadFromString(String string)
	{
		value = string;
	}

	@Override
	public String storeAsString()
	{
		return value;
	}
}
