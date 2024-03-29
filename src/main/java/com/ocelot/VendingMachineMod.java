package com.ocelot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ocelot.gui.ClientGuiOpener;
import com.ocelot.gui.GuiOpener;
import com.ocelot.gui.ServerGuiOpener;
import com.ocelot.init.ClientInit;
import com.ocelot.init.ModBlocks;
import com.ocelot.init.Registry;
import com.ocelot.network.NetworkHandler;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(VendingMachineMod.MOD_ID)
public class VendingMachineMod
{
	public static final String MOD_ID = "ovending";
	public static final int GUI_VENDING_MACHINE_ID = 0;
	public static final int GUI_VENDING_MACHINE_EDIT_ID = 1;
	public static final int GUI_VENDING_MACHINE_INTERNAL_ID = 2;
	
	public static final ItemGroup TAB = new ItemGroup(MOD_ID)
	{
		@Override
		public ItemStack createIcon()
		{
			return new ItemStack(ModBlocks.RED_VENDING_MACHINE);
		}
	};

	private static final Logger LOGGER = LogManager.getLogger();
	private static final GuiOpener GUI_OPENER = DistExecutor.runForDist(() -> () -> new ClientGuiOpener(), () -> () -> new ServerGuiOpener());

	public VendingMachineMod()
	{
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);

		FMLJavaModLoadingContext.get().getModEventBus().register(new Registry());
	}

	public void setup(FMLCommonSetupEvent event)
	{
		NetworkHandler.init();
	}

	public void setupClient(FMLClientSetupEvent event)
	{
		ClientInit.init();
	}

	public static Logger logger()
	{
		return LOGGER;
	}

	public static GuiOpener getGuiOpener()
	{
		return GUI_OPENER;
	}
}