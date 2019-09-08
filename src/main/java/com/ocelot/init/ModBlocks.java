package com.ocelot.init;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.ocelot.VendingMachineMod;
import com.ocelot.blocks.BlockVendingMachine;
import com.ocelot.items.ItemVendingMachine;
import com.ocelot.items.ModItemBlock;
import com.ocelot.tileentity.TileEntityVendingMachine;

import net.minecraft.block.Block;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class ModBlocks
{
	private static final List<Block> BLOCKS = Lists.<Block>newArrayList();

	public static final Block WHITE_VENDING_MACHINE;
	public static final Block ORANGE_VENDING_MACHINE;
	public static final Block MAGENTA_VENDING_MACHINE;
	public static final Block LIGHT_BLUE_VENDING_MACHINE;
	public static final Block YELLOW_VENDING_MACHINE;
	public static final Block LIME_VENDING_MACHINE;
	public static final Block PINK_VENDING_MACHINE;
	public static final Block GRAY_VENDING_MACHINE;
	public static final Block LIGHT_GRAY_VENDING_MACHINE;
	public static final Block CYAN_VENDING_MACHINE;
	public static final Block PURPLE_VENDING_MACHINE;
	public static final Block BLUE_VENDING_MACHINE;
	public static final Block BROWN_VENDING_MACHINE;
	public static final Block GREEN_VENDING_MACHINE;
	public static final Block RED_VENDING_MACHINE;
	public static final Block BLACK_VENDING_MACHINE;

	public static final TileEntityType<TileEntityVendingMachine> TILE_ENTITY_VENDING_MACHINE;

	static
	{
		WHITE_VENDING_MACHINE = new BlockVendingMachine("vending_machine", EnumDyeColor.WHITE);
		ORANGE_VENDING_MACHINE = new BlockVendingMachine("vending_machine", EnumDyeColor.ORANGE);
		MAGENTA_VENDING_MACHINE = new BlockVendingMachine("vending_machine", EnumDyeColor.MAGENTA);
		LIGHT_BLUE_VENDING_MACHINE = new BlockVendingMachine("vending_machine", EnumDyeColor.LIGHT_BLUE);
		YELLOW_VENDING_MACHINE = new BlockVendingMachine("vending_machine", EnumDyeColor.YELLOW);
		LIME_VENDING_MACHINE = new BlockVendingMachine("vending_machine", EnumDyeColor.LIME);
		PINK_VENDING_MACHINE = new BlockVendingMachine("vending_machine", EnumDyeColor.PINK);
		GRAY_VENDING_MACHINE = new BlockVendingMachine("vending_machine", EnumDyeColor.GRAY);
		LIGHT_GRAY_VENDING_MACHINE = new BlockVendingMachine("vending_machine", EnumDyeColor.LIGHT_GRAY);
		CYAN_VENDING_MACHINE = new BlockVendingMachine("vending_machine", EnumDyeColor.CYAN);
		PURPLE_VENDING_MACHINE = new BlockVendingMachine("vending_machine", EnumDyeColor.PURPLE);
		BLUE_VENDING_MACHINE = new BlockVendingMachine("vending_machine", EnumDyeColor.BLUE);
		BROWN_VENDING_MACHINE = new BlockVendingMachine("vending_machine", EnumDyeColor.BROWN);
		GREEN_VENDING_MACHINE = new BlockVendingMachine("vending_machine", EnumDyeColor.GREEN);
		RED_VENDING_MACHINE = new BlockVendingMachine("vending_machine", EnumDyeColor.RED);
		BLACK_VENDING_MACHINE = new BlockVendingMachine("vending_machine", EnumDyeColor.BLACK);

		TILE_ENTITY_VENDING_MACHINE = registerTileEntity("vending_machine", TileEntityVendingMachine::new);
	}

	protected static void init()
	{
		registerBlock(WHITE_VENDING_MACHINE, new ItemVendingMachine(WHITE_VENDING_MACHINE));
		registerBlock(ORANGE_VENDING_MACHINE, new ItemVendingMachine(ORANGE_VENDING_MACHINE));
		registerBlock(MAGENTA_VENDING_MACHINE, new ItemVendingMachine(MAGENTA_VENDING_MACHINE));
		registerBlock(LIGHT_BLUE_VENDING_MACHINE, new ItemVendingMachine(LIGHT_BLUE_VENDING_MACHINE));
		registerBlock(YELLOW_VENDING_MACHINE, new ItemVendingMachine(YELLOW_VENDING_MACHINE));
		registerBlock(LIME_VENDING_MACHINE, new ItemVendingMachine(LIME_VENDING_MACHINE));
		registerBlock(PINK_VENDING_MACHINE, new ItemVendingMachine(PINK_VENDING_MACHINE));
		registerBlock(GRAY_VENDING_MACHINE, new ItemVendingMachine(GRAY_VENDING_MACHINE));
		registerBlock(LIGHT_GRAY_VENDING_MACHINE, new ItemVendingMachine(LIGHT_GRAY_VENDING_MACHINE));
		registerBlock(CYAN_VENDING_MACHINE, new ItemVendingMachine(CYAN_VENDING_MACHINE));
		registerBlock(PURPLE_VENDING_MACHINE, new ItemVendingMachine(PURPLE_VENDING_MACHINE));
		registerBlock(BLUE_VENDING_MACHINE, new ItemVendingMachine(BLUE_VENDING_MACHINE));
		registerBlock(BROWN_VENDING_MACHINE, new ItemVendingMachine(BROWN_VENDING_MACHINE));
		registerBlock(GREEN_VENDING_MACHINE, new ItemVendingMachine(GREEN_VENDING_MACHINE));
		registerBlock(RED_VENDING_MACHINE, new ItemVendingMachine(RED_VENDING_MACHINE));
		registerBlock(BLACK_VENDING_MACHINE, new ItemVendingMachine(BLACK_VENDING_MACHINE));
	}

	public static void registerBlock(Block block)
	{
		if (block.getRegistryName() == null)
			throw new RuntimeException("Block \'" + block.getClass() + "\' is missing a registry name!");
		BLOCKS.add(block);
	}

	public static void registerBlock(Block block, @Nullable Item.Properties properties)
	{
		registerBlock(block, new ModItemBlock(block, properties != null ? properties : new Item.Properties()));
	}

	public static void registerBlock(Block block, Item item)
	{
		registerBlock(block);
		ModItems.registerItem(item.setRegistryName(block.getRegistryName()));
	}

	public static <T extends TileEntity> TileEntityType<T> registerTileEntity(String name, Supplier<T> factory)
	{
		return TileEntityType.register(VendingMachineMod.MOD_ID + ":" + name, TileEntityType.Builder.create(factory));
	}

	public static Block[] getBlocks()
	{
		return BLOCKS.toArray(new Block[0]);
	}
}