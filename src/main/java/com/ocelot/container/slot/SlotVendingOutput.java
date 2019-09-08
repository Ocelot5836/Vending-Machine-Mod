package com.ocelot.container.slot;

import com.ocelot.container.inventory.InventoryVendingMachine;
import com.ocelot.vending.VendingMachine;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotVendingOutput extends Slot
{
	private int previousCount;

	public SlotVendingOutput(VendingMachine vendingMachine, InventoryVendingMachine itemHandler, int index, int xPosition, int yPosition)
	{
		super(itemHandler, index, xPosition, yPosition);
		this.previousCount = 0;
	}

	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return false;
	}

	@Override
	public void onSlotChanged()
	{
		this.previousCount = this.getStack().getCount();
		super.onSlotChanged();
		((InventoryVendingMachine) this.inventory).buyItems(this.getStack().getCount() - this.previousCount);
	}
}