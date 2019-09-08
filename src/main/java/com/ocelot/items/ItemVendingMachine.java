package com.ocelot.items;

import com.ocelot.VendingMachineMod;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class ItemVendingMachine extends ModItemBlock
{
	public ItemVendingMachine(Block block)
	{
		super(block, new Item.Properties().group(VendingMachineMod.TAB));
	}
}