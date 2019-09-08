package com.ocelot.container.inventory;

import com.ocelot.VendingMachineMod;
import com.ocelot.util.InventoryUtils;
import com.ocelot.vending.VendingMachine;
import com.ocelot.vending.VendingMachineItem;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class InventoryVendingMachine implements IInventory
{
	private final VendingMachine vendingMachine;
	private final NonNullList<ItemStack> input;
	private int selectedItem;

	public InventoryVendingMachine(VendingMachine vendingMachine)
	{
		this.vendingMachine = vendingMachine;
		this.input = NonNullList.withSize(1 + 2 * 2, ItemStack.EMPTY);
		this.selectedItem = -1;
		this.resetRecipeAndSlots();
	}

	public void resetRecipeAndSlots()
	{
		VendingMachineItem item = this.vendingMachine.getItem(this.selectedItem);
		ItemStack currency = this.getCurrencyStack(item.getPrice().getItem());
		ItemStack stock = item.getStock();
		if (this.selectedItem != -1 && this.canBuyItems(1) && item.isValidCurrency(currency) && !InventoryUtils.isFull(this.vendingMachine.getInternalInventory()))
		{
			ItemStack output = stock.copy();
			output.setCount(1);
			this.setInventorySlotContents(0, output);
		}
		else
		{
			this.setInventorySlotContents(0, ItemStack.EMPTY);
		}
	}

	public void buyItems(int count)
	{
		if (this.canBuyItems(count))
		{
			VendingMachineItem item = this.vendingMachine.getItem(this.selectedItem);
			item.getStock().shrink(count);
			this.removeCurrency(item.getPrice().getItem(), item.getPrice().getCount() * count);
			InventoryUtils.insertStack(this.vendingMachine.getInternalInventory(), new ItemStack(item.getPrice().getItem(), count), false);
			if (item.getStock().isEmpty())
			{
				this.setSelectedItem(-1);
			}
		}
	}

	public boolean canBuyItems(int count)
	{
		if (count <= 0)
			return false;
		VendingMachineItem item = this.vendingMachine.getItem(this.selectedItem);
		ItemStack currency = this.getCurrencyStack(item.getPrice().getItem());
		ItemStack insertedCurrency = currency.copy();
		insertedCurrency.setCount(count);
		return !item.getStock().isEmpty() && item.isValidCurrency(currency) && currency.getCount() >= item.getPrice().getCount() * count && InventoryUtils.insertStack(this.vendingMachine.getInternalInventory(), insertedCurrency, true).isEmpty();
	}

	public boolean removeCurrency(Item type, int count)
	{
		if (this.getCurrency(type) < count)
			return false;
		for (int i = 0; i < 4; i++)
		{
			if (count == 0)
				break;
			ItemStack stack = this.getStackInSlot(i + 1);
			if (stack.getItem() == type)
			{
				if (stack.getCount() >= count)
				{
					stack.shrink(count);
					count = 0;
				}
				else
				{
					stack.setCount(0);
					count -= stack.getCount();
				}
			}
		}
		return true;
	}

	public int getCurrency(Item type)
	{
		int count = 0;
		for (int i = 0; i < 4; i++)
		{
			ItemStack stack = this.getStackInSlot(i + 1);
			if (stack.getItem() == type)
			{
				count += stack.getCount();
			}
		}
		return count;
	}

	public ItemStack getCurrencyStack(Item item)
	{
		return new ItemStack(item, this.getCurrency(item));
	}

	private boolean inventoryResetNeededOnSlotChange(int slot)
	{
		return slot != 0;
	}

	@Override
	public int getSizeInventory()
	{
		return this.input.size();
	}

	@Override
	public boolean isEmpty()
	{
		for (ItemStack itemstack : this.input)
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
		return this.input.get(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		ItemStack stack = this.getStackInSlot(index);
		if (index == 0 && !stack.isEmpty())
		{
			if (this.canBuyItems(count))
			{
				if (!stack.isEmpty() && this.inventoryResetNeededOnSlotChange(index))
				{
					this.resetRecipeAndSlots();
				}
				return ItemStackHelper.getAndSplit(this.input, index, stack.getCount());
			}
			return ItemStack.EMPTY;
		}
		else
		{
			stack = ItemStackHelper.getAndSplit(this.input, index, count);
			if (!stack.isEmpty() && this.inventoryResetNeededOnSlotChange(index))
			{
				this.resetRecipeAndSlots();
			}
			return stack;
		}
	}

	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		return index > 0 ? ItemStackHelper.getAndRemove(this.input, index) : ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		this.input.set(index, stack);
		if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit())
		{
			stack.setCount(this.getInventoryStackLimit());
		}

		if (this.inventoryResetNeededOnSlotChange(index))
		{
			this.resetRecipeAndSlots();
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
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player)
	{
	}

	@Override
	public void closeInventory(EntityPlayer player)
	{
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		return true;
	}

	@Override
	public void markDirty()
	{
		this.resetRecipeAndSlots();
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
		this.input.clear();
	}

	public int getSelectedItem()
	{
		return selectedItem;
	}

	public void setSelectedItem(int selectedItem)
	{
		this.selectedItem = selectedItem;
		this.resetRecipeAndSlots();
	}
}