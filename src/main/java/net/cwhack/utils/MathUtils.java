package net.cwhack.utils;

public enum MathUtils
{
	;
	public static int clamp(int value, int min, int max) {
		if (value < min) return min;
		return Math.min(value, max);
	}

	public static float clamp(float value, float min, float max) {
		if (value < min) return min;
		return Math.min(value, max);
	}

	public static double clamp(double value, double min, double max) {
		if (value < min) return min;
		return Math.min(value, max);
	}
}
