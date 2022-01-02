package net.cwhack;

import com.mojang.blaze3d.systems.RenderSystem;
import net.cwhack.events.GUIRenderListener;
import net.cwhack.events.UpdateListener;
import net.cwhack.utils.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;

import static net.cwhack.CwHack.CWHACK;
import static net.cwhack.CwHack.MC;

public class NotificationRenderer implements GUIRenderListener, UpdateListener
{

	private String notification = "";
	private boolean shouldDraw = false;
	private int time = 0;

	public NotificationRenderer()
	{
		CWHACK.getEventManager().add(GUIRenderListener.class, this);
		CWHACK.getEventManager().add(UpdateListener.class, this);
	}

	public void sendNotification(String text)
	{
		notification = text;
		shouldDraw = true;
		time = 0;
	}

	@Override
	public void onUpdate()
	{
		if (shouldDraw)
		{
			time++;
			if (time >= 60)
			{
				shouldDraw = false;
				time = 0;
			}
		}
	}

	@Override
	public void onRenderGUI(GUIRenderEvent event)
	{
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		int scale = MC.options.guiScale;
		if (shouldDraw)
		{
			int height = scale * 32;
			int width = MC.getWindow().getFramebufferWidth();
			if (time < 5)
			{
				width += scale * (192 * (1 - (time + event.getPartialTicks()) / 5f));
			}
			if (time >= 55)
			{
				width += scale * (192 * (1 - (60 - time - event.getPartialTicks()) / 5f));
			}
			RenderSystem.setShaderColor(0.4f, 0.4f, 1, 0.8f);
			RenderUtils.drawQuad(width - scale * 192, height, width, height + scale * 32, new MatrixStack());
			RenderUtils.drawString(notification, width - scale * 188, height + 12 * scale, 0xffffff, scale);
		}
	}
}
