package net.cwhack.features;

import net.cwhack.events.GUIRenderListener;
import net.cwhack.events.RenderListener;
import net.cwhack.events.RenderNameTagListener;
import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;
import net.cwhack.setting.DecimalSetting;
import net.cwhack.utils.NameTagUtils;
import net.cwhack.utils.RenderUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static net.cwhack.CwHack.MC;

public class NameTagFeature extends Feature implements RenderListener, GUIRenderListener, RenderNameTagListener, UpdateListener
{

	private DecimalSetting scale = new DecimalSetting("scale", "scale of the nametags", 1.0, this);

	private ArrayList<Entity> players = new ArrayList<>();

	public NameTagFeature()
	{
		super("NameTag", "display armor and holding item of players");
		addSetting(scale);
	}

	@Override
	protected void onEnable()
	{
		eventManager.add(RenderListener.class, this);
		eventManager.add(GUIRenderListener.class, this);
		eventManager.add(UpdateListener.class, this);
	}

	@Override
	protected void onDisable()
	{
		eventManager.remove(RenderListener.class, this);
		eventManager.remove(GUIRenderListener.class, this);
		eventManager.remove(UpdateListener.class, this);
	}

	@Override
	public void onUpdate()
	{
		players = StreamSupport.stream(MC.world.getEntities().spliterator(), true)
				.filter(e -> e instanceof PlayerEntity)
				.filter(e -> e != MC.player)
				.collect(Collectors.toCollection(ArrayList::new));
		players.sort(Comparator.comparingDouble(e -> 1.0 / MC.player.squaredDistanceTo(e)));
	}

	@Override
	public void onRender(RenderEvent event)
	{
	}

	@Override
	public void onRenderGUI(GUIRenderEvent event)
	{
		players.forEach(e ->
				render(e, event.getPartialTicks()));
	}

	@Override
	public void onRenderNameTag()
	{
	}

	private void render(Entity e, float tickDelta)
	{
		float scaleV = scale.getValueF();

		int size = (int) (16 * scaleV);
		Vec3d delta = e.getPos().subtract(e.prevX, e.prevY, e.prevZ);
		Vec3d pos = NameTagUtils.to2D(e.getPos().add(0.0, e.getHeight() + 0.5, 0.0).add(delta.multiply(tickDelta)));
		if (pos == null)
			return;

		int x = (int) pos.x;
		int y = (int) pos.y;
		int mainHandX = x - size * 3;
		int helmetX = x - size * 2;
		int chestplateX = x - size;
		int leggingsX = x;
		int bootsX = x + size;
		int offHandX = x + size * 2;
		PlayerEntity player = (PlayerEntity) e;
		ItemStack mainHand = player.getMainHandStack();
		ItemStack helmet = player.getInventory().armor.get(3);
		ItemStack chestplate = player.getInventory().armor.get(2);
		ItemStack leggings = player.getInventory().armor.get(1);
		ItemStack boots = player.getInventory().armor.get(0);
		ItemStack offHand = player.getOffHandStack();
		RenderUtils.drawItem(mainHand, mainHandX, y, scaleV, true);
		RenderUtils.drawItem(helmet, helmetX, y, scaleV, true);
		RenderUtils.drawItem(chestplate, chestplateX, y, scaleV, true);
		RenderUtils.drawItem(leggings, leggingsX, y, scaleV, true);
		RenderUtils.drawItem(boots, bootsX, y, scaleV, true);
		RenderUtils.drawItem(offHand, offHandX, y, scaleV, true);

		int health = (int) (player.getHealth() + player.getAbsorptionAmount());
		String healthStr = Integer.toString(health);
		int color = 0x00FF00;
		if (health <= 12)
			color = 0xFF0000;

		RenderUtils.drawString(Integer.toString(health), (int) (x - MC.textRenderer.getWidth(healthStr) * scaleV / 2.0f), (int) (y - 8 * scaleV), color, scaleV);
		RenderUtils.drawString(e.getEntityName(), (int) (x - MC.textRenderer.getWidth(e.getEntityName()) * scaleV / 2.0f), (int) (y - 20 * scaleV), 0xFFFFFF, scaleV);
	}
}
