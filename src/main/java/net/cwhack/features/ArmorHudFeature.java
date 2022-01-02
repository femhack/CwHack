package net.cwhack.features;

import net.cwhack.events.GUIRenderListener;
import net.cwhack.feature.Feature;
import net.cwhack.utils.RenderUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

import static net.cwhack.CwHack.MC;

public class ArmorHudFeature extends Feature implements GUIRenderListener
{

	public ArmorHudFeature()
	{
		super("ArmorHud", "display armor durability on your screen");
	}

	@Override
	protected void onEnable()
	{
		eventManager.add(GUIRenderListener.class, this);
	}

	@Override
	protected void onDisable()
	{
		eventManager.remove(GUIRenderListener.class, this);
	}

	@Override
	public void onRenderGUI(GUIRenderEvent event)
	{
		render();
	}

	private void render()
	{
		PlayerEntity player = MC.player;

		ArrayList<ItemStack> items = new ArrayList<>();
		items.add(player.getMainHandStack());
		items.add(player.getInventory().armor.get(3));
		items.add(player.getInventory().armor.get(2));
		items.add(player.getInventory().armor.get(1));
		items.add(player.getInventory().armor.get(0));
		items.add(player.getOffHandStack());

		float scale = 2;
		int y = 400;

		for (ItemStack item : items)
		{
			RenderUtils.drawItem(item, 0, y, scale, true);
			y += scale * 16;
		}
	}
}
