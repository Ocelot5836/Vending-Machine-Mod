package com.ocelot.container;

import com.ocelot.blocks.BlockVendingMachine;
import com.ocelot.container.inventory.InventoryVendingMachineEdit;
import com.ocelot.vending.VendingMachine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerVendingMachineEdit extends Container
{
	private VendingMachine vendingMachine;
	private IInventory vendingMachineInventory;

	public ContainerVendingMachineEdit(EntityPlayer player, VendingMachine vendingMachine)
	{
		this.vendingMachine = vendingMachine;
		this.vendingMachineInventory = new InventoryVendingMachineEdit(vendingMachine);
		this.vendingMachineInventory.openInventory(player);

		for (int y = 0; y < 4; y++)
		{
			for (int x = 0; x < 3; x++)
			{
				this.addSlot(new Slot(this.vendingMachineInventory, x + y * 3, 32 + x * 18, 18 + y * 22));
			}
		}

		for (int y = 0; y < 3; y++)
		{
			for (int x = 0; x < 9; x++)
			{
				this.addSlot(new Slot(player.inventory, 9 + x + y * 9, 8 + x * 18, 114 + y * 18));
			}
		}

		for (int x = 0; x < 9; x++)
		{
			this.addSlot(new Slot(player.inventory, x, 8 + x * 18, 172));
		}
	}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);
		this.vendingMachineInventory.closeInventory(player);
		this.vendingMachine.setEditing(false);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index)
	{
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index < this.vendingMachineInventory.getSizeInventory())
			{
				if (!this.mergeItemStack(itemstack1, this.vendingMachineInventory.getSizeInventory(), this.inventorySlots.size(), true))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!this.mergeItemStack(itemstack1, 0, this.vendingMachineInventory.getSizeInventory(), false))
			{
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty())
			{
				slot.putStack(ItemStack.EMPTY);
			}
			else
			{
				slot.onSlotChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount())
			{
				return ItemStack.EMPTY;
			}

			slot.onTake(player, itemstack1);
		}

		return itemstack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		if (this.vendingMachineInventory.isUsableByPlayer(player) && player.world.getBlockState(this.vendingMachine.getTe().getPos()).getBlock() instanceof BlockVendingMachine)
		{
			return player.getDistanceSq((double) this.vendingMachine.getTe().getPos().getX() + 0.5D, (double) this.vendingMachine.getTe().getPos().getY() + 0.5D, (double) this.vendingMachine.getTe().getPos().getZ() + 0.5D) <= 64.0D;
		}
		return false;
	}

	public IInventory getVendingMachineInventory()
	{
		return vendingMachineInventory;
	}
}