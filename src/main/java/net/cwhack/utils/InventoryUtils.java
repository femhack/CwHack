package net.cwhack.utils;

import net.cwhack.CwHack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public enum InventoryUtils
{
	;

	public static boolean selectItemFromHotbar(Predicate<Item> item)
	{
		PlayerInventory inv = CwHack.MC.player.getInventory();

		for (int i = 0; i < 9; i++)
		{
			ItemStack itemStack = inv.getStack(i);
			if (!item.test(itemStack.getItem()))
				continue;
			inv.selectedSlot = i;
			return true;
		}

		return false;
	}

	public static boolean selectItemFromHotbar(Item item)
	{
		return selectItemFromHotbar(i -> i == item);
	}

	public static boolean hasItemInHotbar(Predicate<Item> item)
	{
		PlayerInventory inv = CwHack.MC.player.getInventory();

		for (int i = 0; i < 9; i++)
		{
			ItemStack itemStack = inv.getStack(i);
			if (item.test(itemStack.getItem()))
				return true;
		}
		return false;
	}
}
