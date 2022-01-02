package net.cwhack.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.cwhack.mixinterface.IMatrix4f;
import net.cwhack.utils.math.Vec4d;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import static net.cwhack.CwHack.MC;

public enum NameTagUtils
{
	;
	private static Vec3d camera = new Vec3d(0, 0, 0);
	private static Vec3d cameraNegated = new Vec3d(0, 0, 0);
	private static Matrix4f model;
	private static Matrix4f projection;
	private static double windowScale;
	private static double scale;

	public static void onRender(MatrixStack matrices, Matrix4f projection)
	{
		model = matrices.peek().getModel().copy();
		NameTagUtils.projection = projection;
		camera = MC.gameRenderer.getCamera().getPos();
		cameraNegated = camera.multiply(new Vec3d(-1, -1, -1));
		windowScale = MC.getWindow().calculateScaleFactor(1, MC.forcesUnicodeFont());
	}

	@Nullable
	public static Vec3d to2D(Vec3d pos)
	{
		scale = getScale(pos);

		Vec4d vec4 = new Vec4d(cameraNegated.x + pos.x, cameraNegated.y + pos.y, cameraNegated.z + pos.z, 1);
		vec4 = ((IMatrix4f) (Object) projection).multiply(((IMatrix4f) (Object) model).multiply(vec4));

		if (vec4.w <= 0.0)
			return null;

		vec4 = vec4.toScreen();
		double x = vec4.x * MC.getWindow().getFramebufferWidth();
		double y = vec4.y * MC.getWindow().getFramebufferHeight();

		if (Double.isInfinite(x) || Double.isInfinite(y))
			return null;

		return new Vec3d(x / windowScale, MC.getWindow().getFramebufferHeight() - y / windowScale, vec4.z);
	}

	public static void begin(Vec3d pos) {
		MatrixStack matrices = RenderSystem.getModelViewStack();

		matrices.push();
		matrices.translate(pos.x, pos.y, 0);
		matrices.scale((float) scale, (float) scale, 1);
	}

	public static void end() {
		RenderSystem.getModelViewStack().pop();
	}

	private static double getScale(Vec3d pos)
	{
		double dist = camera.distanceTo(pos);
		return MathUtils.clamp(1 - dist * 0.01, 0.5, Integer.MAX_VALUE);
	}
}
