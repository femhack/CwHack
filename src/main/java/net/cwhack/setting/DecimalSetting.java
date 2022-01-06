package net.cwhack.setting;

import net.cwhack.feature.Feature;

public class DecimalSetting extends Setting<Double>
{
	private double value;

	public DecimalSetting(String name, String description, double defaultValue, Feature feature)
	{
		super(name, description, feature);
		this.value = defaultValue;
	}

	@Override
	public void loadFromStringInternal(String string)
	{
		value = Double.parseDouble(string);
	}

	@Override
	public String storeAsString()
	{
		return Double.toString(value);
	}

	@Override
	public Double getValue()
	{
		return value;
	}

	public float getValueF()
	{
		return (float) value;
	}

	public interface ValueDisplayType
	{
		ValueDisplayType DECIMAL =
				v -> Math.round(v * 1e6) / 1e6 + "";

		ValueDisplayType INTEGER = v -> (int)v + "";

		ValueDisplayType PERCENTAGE =
				v -> (int)(Math.round(v * 1e8) / 1e6) + "%";

		ValueDisplayType DEGREES = v -> (int)v + "\u00b0";

		ValueDisplayType NONE = v -> "";

		String getValueString(double value);
	}
}
