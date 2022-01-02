package net.cwhack.utils.math;

public class Vec4d
{
	public final double x, y, z, w;

	public Vec4d(double x, double y, double z, double w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Vec4d toScreen() {
		double newW = 1.0 / w * 0.5;

		double newX = x * newW + 0.5;
		double newY = y * newW + 0.5;
		double newZ = z * newW + 0.5;

		return new Vec4d(newX, newY, newZ, newW);
	}
}
