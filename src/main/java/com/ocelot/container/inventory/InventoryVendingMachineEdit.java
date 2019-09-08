package com.ocelot.container.inventory;

import com.ocelot.VendingMachineMod;
import com.ocelot.vending.VendingMachine;
import com.ocelot.vending.VendingMachineItem;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class InventoryVendingMachineEdit implements IInventory
{
	private final VendingMachine vendingMachine;
	private final NonNullList<ItemStack> inventory;

	public InventoryVendingMachineEdit(VendingMachine vendingMachine)
	{
		this.vendingMachine = vendingMachine;
		this.inventory = NonNullList.withSize(4 * 3, ItemStack.EMPTY);

		for (int y = 0; y < 4; y++)
		{
			for (int x = 0; x < 3; x++)
			{
				this.setInventorySlotContents(x + y * 3, this.vendingMachine.getItem(x + y * 3).getStock().copy());
			}
		}
	}

	private void setSlotContents()
	{
		for (int y = 0; y < 4; y++)
		{
			for (int x = 0; x < 3; x++)
			{
				VendingMachineItem item = this.vendingMachine.getItem(x + y * 3);
				ItemStack stock = this.getStackInSlot(x + y * 3);

				if (!ItemStack.areItemStacksEqual(item.getStock(), stock))
				{
					this.vendingMachine.setStock(stock, x + y * 3);
				}

				// TODO set price
				// if (!ItemStack.areItemStacksEqual(item.getStock(), price))
				// {
				// this.vendingMachine.setPrice(price, x + y * 3);
				// }
			}
		}
	}

	@Override
	public int getSizeInventory()
	{
		return this.inventory.size();
	}

	@Override
	public boolean isEmpty()
	{
		for (ItemStack itemstack : this.inventory)
		{
			if (!itemstack.isEmpty())
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getStackInSlot(int index)
	{
		return this.inventory.get(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		return ItemStackHelper.getAndSplit(this.inventory, index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		return ItemStackHelper.getAndRemove(this.inventory, index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		this.inventory.set(index, stack);
		if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit())
		{
			stack.setCount(this.getInventoryStackLimit());
		}
	}

	@Override
	public ITextComponent getName()
	{
		return new TextComponentTranslation("gui." + VendingMachineMod.MOD_ID + ".vending_machine");
	}

	@Override
	public boolean hasCustomName()
	{
		return false;
	}

	@Override
	public ITextComponent getCustomName()
	{
		return null;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		return this.vendingMachine.getOwner() == null || this.vendingMachine.getOwner().equals(player.getUniqueID());
	}

	@Override
	public void openInventory(EntityPlayer player)
	{
	}

	@Override
	public void closeInventory(EntityPlayer player)
	{
		this.setSlotContents();
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		return true;
	}

	@Override
	public void markDirty()
	{
		this.setSlotContents();
	}

	@Override
	public int getField(int id)
	{
		return 0;
	}

	@Override
	public void setField(int id, int value)
	{
	}

	@Override
	public int getFieldCount()
	{
		return 0;
	}

	@Override
	public void clear()
	{
		this.inventory.clear();
	}
}