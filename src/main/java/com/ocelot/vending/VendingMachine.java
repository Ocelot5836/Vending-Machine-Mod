package com.ocelot.vending;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class VendingMachine implements INBTSerializable<NBTTagCompound>
{
	private NonNullList<VendingMachineItem> inventory;
	private ItemStackHandler internal;
	private UUID owner;
	private String ownerName;
	private TileEntity te;
	private boolean editing;

	public VendingMachine(TileEntity te)
	{
		this.inventory = NonNullList.withSize(3 * 4, VendingMachineItem.EMPTY);
		this.internal = new ItemStackHandler(9 * 3)
		{
			@Override
			protected void onContentsChanged(int slot)
			{
				te.markDirty();
			}
		};
		this.owner = null;
		this.ownerName = null;
		this.te = te;
		this.editing = false;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();

		{
			NBTTagList inventoryTag = new NBTTagList();
			for (int i = 0; i < this.inventory.size(); i++)
			{
				inventoryTag.add(this.inventory.get(i).serializeNBT());
			}
			nbt.setTag("inventory", inventoryTag);
		}

		nbt.setTag("internal", this.internal.serializeNBT());
		if (this.owner != null)
		{
			if (this.te.getWorld() != null && this.te.getWorld().getPlayerEntityByUUID(this.owner) != null)
			{
				this.ownerName = this.te.getWorld().getPlayerEntityByUUID(this.owner).getName().getFormattedText();
			}
			nbt.setUniqueId("owner", this.owner);
			nbt.setString("ownerName", this.ownerName);
		}

		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		NBTTagList inventoryTag = nbt.getList("inventory", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < inventoryTag.size(); i++)
		{
			this.inventory.set(i, new VendingMachineItem(inventoryTag.getCompound(i)));
		}

		this.internal.deserializeNBT(nbt.getCompound("internal"));
		if (nbt.hasUniqueId("owner"))
		{
			this.owner = nbt.getUniqueId("owner");
			if (this.te.getWorld() != null && this.te.getWorld().getPlayerEntityByUUID(this.owner) != null)
			{
				this.ownerName = this.te.getWorld().getPlayerEntityByUUID(this.owner).getName().getFormattedText();
			}
			else if (nbt.contains("ownerName", Constants.NBT.TAG_STRING))
			{
				this.ownerName = nbt.getString("ownerName");
			}
		}
	}

	/**
	 * @param slot
	 *            The slot to get the item from
	 * @return The item at that slot or empty if there is no item at that slot
	 */
	public VendingMachineItem getItem(int slot)
	{
		if (slot < 0 || slot >= this.inventory.size())
			return VendingMachineItem.EMPTY;
		return this.inventory.get(slot);
	}

	/**
	 * @return The internal inventory of this vending machine
	 */
	public IItemHandler getInternalInventory()
	{
		return internal;
	}

	/**
	 * @return The uuid of the owner
	 */
	@Nullable
	public UUID getOwner()
	{
		return owner;
	}

	/**
	 * @return The name of the owner
	 */
	@Nullable
	public String getOwnerName()
	{
		return ownerName;
	}

	/**
	 * @return The tile entity hosting this vending machine
	 */
	public TileEntity getTe()
	{
		return te;
	}

	/**
	 * @return Whether or not this vending machine is being currently edited
	 */
	public boolean isEditing()
	{
		return editing;
	}

	/**
	 * Sets the owner uuid.
	 * 
	 * @param owner
	 *            The new owner
	 */
	public void setOwner(@Nullable EntityPlayer owner)
	{
		this.owner = owner == null ? null : owner.getUniqueID();
		this.ownerName = owner == null ? null : owner.getName().getFormattedText();
		this.te.markDirty();
	}

	/**
	 * Sets the selling stack at the specified slot.
	 * 
	 * @param stack
	 *            The new stack to be sold
	 * @param slot
	 *            The slot to get the item from
	 */
	public void setStock(ItemStack stack, int slot)
	{
		if (slot < 0 || slot >= this.inventory.size())
			return;
		VendingMachineItem previousItem = this.inventory.get(slot);
		this.inventory.set(slot, new VendingMachineItem(stack, previousItem.getPrice()));
		this.te.markDirty();
	}

	/**
	 * Sets the selling price at the specified slot.
	 * 
	 * @param stack
	 *            The new price of the stack in the specified slot
	 * @param slot
	 *            The slot to get the item from
	 */
	public void setPrice(ItemStack stack, int slot)
	{
		if (slot < 0 || slot >= this.inventory.size())
			return;
		VendingMachineItem previousItem = this.inventory.get(slot);
		this.inventory.set(slot, new VendingMachineItem(previousItem.getStock(), stack));
		this.te.markDirty();
	}

	/**
	 * Sets whether or not this vending machine is being edited.
	 * 
	 * @param editing
	 *            Whether or not the vending machine should now be editing
	 */
	public void setEditing(boolean editing)
	{
		this.editing = editing;
	}
}