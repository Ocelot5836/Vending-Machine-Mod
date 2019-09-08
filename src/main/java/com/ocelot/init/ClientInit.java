package com.ocelot.init;

import com.ocelot.tileentity.TileEntityVendingMachine;
import com.ocelot.tileentity.render.RenderVendingMachine;

import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientInit
{
	public static void init()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityVendingMachine.class, new RenderVendingMachine());
	}
}