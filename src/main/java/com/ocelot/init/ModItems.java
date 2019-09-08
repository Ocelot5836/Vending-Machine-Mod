package com.ocelot.init;

import java.util.List;

import com.google.common.collect.Lists;
import com.ocelot.VendingMachineMod;
import com.ocelot.items.ModItem;

import net.minecraft.item.Item;

public class ModItems
{
	private static final List<Item> ITEMS = Lists.<Item>newArrayList();

	public static final Item KEY;
	public static final Item VENDING_MACHINE_WINDOW;

	static
	{
		KEY = new ModItem("key", new Item.Properties().maxStackSize(1).group(VendingMachineMod.TAB));
		VENDING_MACHINE_WINDOW = new ModItem("vending_machine_window", new Item.Properties().group(VendingMachineMod.TAB));
	}

	protected static void init()
	{
		registerItem(KEY);
		registerItem(VENDING_MACHINE_WINDOW);
	}

	public static void registerItem(Item item)
	{
		if (item.getRegistryName() == null)
			throw new RuntimeException("Item \'" + item.getClass() + "\' is missing a registry name!");
		ITEMS.add(item);
	}

	public static Item[] getItems()
	{
		return ITEMS.toArray(new Item[0]);
	}
}