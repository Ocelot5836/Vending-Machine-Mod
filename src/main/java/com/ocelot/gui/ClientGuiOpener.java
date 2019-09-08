package com.ocelot.gui;

import javax.annotation.Nullable;

import com.ocelot.VendingMachineMod;
import com.ocelot.tileentity.TileEntityVendingMachine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class ClientGuiOpener extends ServerGuiOpener
{
	@Override
	public void openGui(int guiId, EntityPlayer player, IWorld world, BlockPos pos)
	{
		super.openGui(guiId, player, world, pos);

		if (world.isRemote())
		{
			GuiScreen value = getGuiElement(guiId, player, world, pos);
			if (value != null)
			{
				Minecraft.getInstance().displayGuiScreen(value);
			}
		}
	}

	@Nullable
	private static GuiScreen getGuiElement(int id, EntityPlayer player, IWorld world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);
		if (id == VendingMachineMod.GUI_VENDING_MACHINE_ID)
			return new GuiVendingMachine(player, ((TileEntityVendingMachine) te).getVendingMachine());
		if (id == VendingMachineMod.GUI_VENDING_MACHINE_EDIT_ID)
			return new GuiVendingMachineEdit(player, ((TileEntityVendingMachine) te).getVendingMachine());
		if (id == VendingMachineMod.GUI_VENDING_MACHINE_INTERNAL_ID)
			return new GuiVendingMachineInternal(player, ((TileEntityVendingMachine) te).getVendingMachine());
		return null;
	}
}