package net.cwhack.features;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.cwhack.events.UpdateListener;
import net.cwhack.feature.Feature;
import net.cwhack.setting.IntegerSetting;
import net.cwhack.setting.TextSetting;
import net.cwhack.utils.ChatUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static net.cwhack.CwHack.MC;

public class CustomItemFeature extends Feature implements UpdateListener
{
	private final TextSetting itemName = new TextSetting("itemName", "the item name", "", this);
	private final TextSetting nbt = new TextSetting("nbt", "the nbt", "", this);
	private final IntegerSetting count = new IntegerSetting("count", "the number of the item stack", 1, this);

	public CustomItemFeature()
	{
		super("CustomItem", "uses custom NBT to create item under creative mode");
		addSetting(itemName);
		addSetting(nbt);
		addSetting(count);
	}

	@Override
	protected void onEnable()
	{
		eventManager.add(UpdateListener.class, this);
	}

	@Override
	protected void onDisable()
	{
		eventManager.remove(UpdateListener.class, this);
	}

	@Override
	public void onUpdate()
	{
		if (!MC.player.getAbilities().creativeMode)
		{
			setEnabled(false);
			ChatUtils.error("only works in creative mode");
			return;
		}

		ItemStack item = new ItemStack(Registry.ITEM.get(new Identifier(itemName.getValue())));
		try
		{
			item.setNbt(StringNbtReader.parse(nbt.getValue()));
		} catch (CommandSyntaxException e)
		{
			ChatUtils.error(e.getLocalizedMessage());
			setEnabled(false);
			return;
		}
		item.setCount(count.getValue());

		for (int i = 0; i < 9; i++)
		{
			if (!MC.player.getInventory().getStack(i).isEmpty())
				continue;

			MC.getNetworkHandler().sendPacket(new CreativeInventoryActionC2SPacket(36 + i, item));
			setEnabled(false);
			return;
		}

		ChatUtils.error("insufficient hotbar space");
		setEnabled(false);
	}
}
