package com.ocelot.tileentity;

import com.ocelot.init.ModBlocks;
import com.ocelot.util.ISimpleInventory;
import com.ocelot.vending.VendingMachine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.IChestLid;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;

public class TileEntityVendingMachine extends ModTileEntity implements ITickable, IChestLid, ISimpleInventory
{
	private VendingMachine vendingMachine;

	private float previousDoorAngle;
	private float doorAngle;

	public TileEntityVendingMachine()
	{
		super(ModBlocks.TILE_ENTITY_VENDING_MACHINE);
		this.vendingMachine = new VendingMachine(this);
	}

	@Override
	public void tick()
	{
		this.previousDoorAngle = this.doorAngle;

		if (this.vendingMachine.isEditing())
		{
			if (this.doorAngle == 0)
			{
				this.world.playSound((EntityPlayer) null, this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.1F, this.world.rand.nextFloat() * 0.1F + 1.1F);
			}
			if (this.doorAngle < 1)
			{
				this.doorAngle += 0.1;
				if (this.doorAngle > 1)
					this.doorAngle = 1;
			}
		}
		else if (this.doorAngle > 0)
		{
			this.doorAngle -= 0.1f;
			if (this.doorAngle < 0)
				this.doorAngle = 0;
		}

		if (this.doorAngle < 0.5F && this.previousDoorAngle >= 0.5F)
		{
			this.world.playSound((EntityPlayer) null, this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.1F, this.world.rand.nextFloat() * 0.1F + 1.1F);
		}
	}

	@Override
	public void read(NBTTagCompound nbt)
	{
		super.read(nbt);
		this.vendingMachine.deserializeNBT(nbt.getCompound("vendingMachine"));
	}

	@Override
	public NBTTagCompound write(NBTTagCompound nbt)
	{
		super.write(nbt);
		nbt.setTag("vendingMachine", this.vendingMachine.serializeNBT());
		return nbt;
	}

	@Override
	public float getLidAngle(float partialTicks)
	{
		return (this.previousDoorAngle + (this.doorAngle - this.previousDoorAngle) * partialTicks);
	}

	public VendingMachine getVendingMachine()
	{
		return vendingMachine;
	}

	@Override
	public int getSlots()
	{
		return 4 * 3 + this.vendingMachine.getInternalInventory().getSlots();
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		if (slot < 0 || slot >= this.getSlots())
			return ItemStack.EMPTY;
		return slot < 3 * 4 ? this.vendingMachine.getItem(slot).getStock() : this.vendingMachine.getInternalInventory().getStackInSlot(slot - 3 * 4);
	}
}