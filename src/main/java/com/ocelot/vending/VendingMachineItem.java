package com.ocelot.vending;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

public class VendingMachineItem implements INBTSerializable<NBTTagCompound>
{
	public static final VendingMachineItem EMPTY = new VendingMachineItem(ItemStack.EMPTY, ItemStack.EMPTY);

	private ItemStack stock;
	private ItemStack price;

	public VendingMachineItem(NBTTagCompound nbt)
	{
		this(ItemStack.EMPTY, ItemStack.EMPTY);
		this.deserializeNBT(nbt);
	}

	public VendingMachineItem(ItemStack stock, ItemStack price)
	{
		this.stock = stock;
		this.price = price;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();

		if (!this.stock.isEmpty())
		{
			nbt.setTag("stock", this.stock.serializeNBT());
		}

		if (!this.price.isEmpty())
		{
			nbt.setTag("price", this.price.serializeNBT());
		}

		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		if (nbt.contains("stock", Constants.NBT.TAG_COMPOUND))
		{
			this.stock = ItemStack.read(nbt.getCompound("stock"));
		}

		if (nbt.contains("price", Constants.NBT.TAG_COMPOUND))
		{
			this.price = ItemStack.read(nbt.getCompound("price"));
		}
	}

	public boolean isValidCurrency(ItemStack currency)
	{
		return this.price.isEmpty() || (ItemStack.areItemsEqual(this.price, currency) && this.price.getCount() <= currency.getCount());
	}

	public ItemStack getStock()
	{
		return stock;
	}

	public ItemStack getPrice()
	{
		return price;
	}
}