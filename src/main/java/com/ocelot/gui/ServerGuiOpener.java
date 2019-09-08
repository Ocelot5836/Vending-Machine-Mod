package com.ocelot.gui;

import javax.annotation.Nullable;

import com.ocelot.VendingMachineMod;
import com.ocelot.container.ContainerVendingMachine;
import com.ocelot.container.ContainerVendingMachineEdit;
import com.ocelot.container.ContainerVendingMachineInternal;
import com.ocelot.network.MessageOpenGui;
import com.ocelot.network.NetworkHandler;
import com.ocelot.tileentity.TileEntityVendingMachine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.fml.network.NetworkDirection;

public class ServerGuiOpener implements GuiOpener
{
	@Override
	public void openGui(int guiId, EntityPlayer player, IWorld world, BlockPos pos)
	{
		if (!world.isRemote())
		{
			Container value = getGuiElement(guiId, player, world, pos);
			if (value != null)
			{
				if (player.openContainer != player.inventoryContainer)
				{
					player.closeScreen();
				}

				player.openContainer = value;
				if (player instanceof EntityPlayerMP)
				{
					EntityPlayerMP playerMP = (EntityPlayerMP) player;
					playerMP.getNextWindowId();
					playerMP.openContainer.windowId = playerMP.currentWindowId;
					NetworkHandler.INSTANCE.sendTo(new MessageOpenGui(playerMP.currentWindowId), playerMP.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
					value.addListener((EntityPlayerMP) player);
				}
			}
		}
	}

	@Nullable
	private static Container getGuiElement(int id, EntityPlayer player, IWorld world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);
		if (id == VendingMachineMod.GUI_VENDING_MACHINE_ID)
			return new ContainerVendingMachine(player, ((TileEntityVendingMachine) te).getVendingMachine());
		if (id == VendingMachineMod.GUI_VENDING_MACHINE_EDIT_ID)
			return new ContainerVendingMachineEdit(player, ((TileEntityVendingMachine) te).getVendingMachine());
		if (id == VendingMachineMod.GUI_VENDING_MACHINE_INTERNAL_ID)
			return new ContainerVendingMachineInternal(player, ((TileEntityVendingMachine) te).getVendingMachine());
		return null;
	}
}