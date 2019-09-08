package com.ocelot.container;

import com.ocelot.blocks.BlockVendingMachine;
import com.ocelot.vending.VendingMachine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerVendingMachineInternal extends Container
{
	private VendingMachine vendingMachine;

	public ContainerVendingMachineInternal(EntityPlayer player, VendingMachine vendingMachine)
	{
		this.vendingMachine = vendingMachine;

		for (int y = 0; y < 3; y++)
		{
			for (int x = 0; x < 9; x++)
			{
				this.addSlot(new SlotItemHandler(this.vendingMachine.getInternalInventory(), x + y * 9, 8 + x * 18, 18 + y * 18));
			}
		}

		for (int y = 0; y < 3; y++)
		{
			for (int x = 0; x < 9; x++)
			{
				this.addSlot(new Slot(player.inventory, 9 + x + y * 9, 8 + x * 18, 84 + y * 18));
			}
		}

		for (int x = 0; x < 9; x++)
		{
			this.addSlot(new Slot(player.inventory, x, 8 + x * 18, 142));
		}
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

			if (index < this.vendingMachine.getInternalInventory().getSlots())
			{
				if (!this.mergeItemStack(itemstack1, this.vendingMachine.getInternalInventory().getSlots(), this.inventorySlots.size(), true))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!this.mergeItemStack(itemstack1, 0, this.vendingMachine.getInternalInventory().getSlots(), false))
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
		if (player.world.getBlockState(this.vendingMachine.getTe().getPos()).getBlock() instanceof BlockVendingMachine)
		{
			return player.getDistanceSq((double) this.vendingMachine.getTe().getPos().getX() + 0.5D, (double) this.vendingMachine.getTe().getPos().getY() + 0.5D, (double) this.vendingMachine.getTe().getPos().getZ() + 0.5D) <= 64.0D;
		}
		return false;
	}
}