package com.ocelot.gui;

import java.util.List;

import javax.annotation.Nullable;

import com.ocelot.VendingMachineMod;
import com.ocelot.container.ContainerVendingMachine;
import com.ocelot.container.inventory.InventoryVendingMachine;
import com.ocelot.network.MessageSetVendingItem;
import com.ocelot.network.NetworkHandler;
import com.ocelot.util.InventoryUtils;
import com.ocelot.vending.VendingMachine;
import com.ocelot.vending.VendingMachineItem;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class GuiVendingMachine extends GuiContainer
{
	public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(VendingMachineMod.MOD_ID, "textures/gui/container/vending_machine.png");
	public static final int HIGHLIGHT_COLOR = 0x3cf6ff00;

	private InventoryVendingMachine vendingMachineInventory;
	private VendingMachine vendingMachine;

	public GuiVendingMachine(EntityPlayer player, VendingMachine vendingMachine)
	{
		super(new ContainerVendingMachine(player, vendingMachine));
		this.vendingMachineInventory = ((ContainerVendingMachine) this.inventorySlots).getVendingMachineInventory();
		this.vendingMachine = vendingMachine;
	}

	@Override
	protected void initGui()
	{
		this.xSize = 176;
		this.ySize = 196;
		super.initGui();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		super.drawDefaultBackground();
		GlStateManager.color4f(1, 1, 1, 1);
		super.render(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);

		int hoveredIndex = this.getHoveredIndex(mouseX, mouseY);
		if (hoveredIndex != -1)
		{
			List<String> tooltip = this.getItemToolTip(hoveredIndex);
			if (tooltip != null)
			{
				this.drawHoveringText(tooltip, mouseX, mouseY);
			}
		}

		// if (mouseX >= this.guiLeft + 99 && mouseX < this.guiLeft + 99 + 36 && mouseY >= this.guiTop + 63 && mouseY < this.guiTop + 63 + 36)
		// {
		// for (int i = 0; i < 4; i++)
		// {
		// int x = this.guiLeft + 99 + (i % 2 * 18);
		// int y = this.guiTop + 63 + (i / 2 * 18);
		// if (mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + 18)
		// {
		// ItemStack stack = this.vendingMachineInventory.getStackInSlot(i + 1);
		// if (stack.isEmpty())
		// {
		// this.drawHoveringText(TextFormatting.GRAY + I18n.format("gui." + VendingMachineMod.MOD_ID + ".vending_machine.currency"), mouseX, mouseY);
		// }
		// break;
		// }
		// }
		// }
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		this.mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String title = this.vendingMachineInventory.getDisplayName().getFormattedText();
		this.fontRenderer.drawString(title, (this.width - this.mc.fontRenderer.getStringWidth(title)) / 2 - this.guiLeft, 6.0F, 4210752);
		this.fontRenderer.drawString(this.mc.player.inventory.getDisplayName().getFormattedText(), 8.0F, (float) (this.ySize - 96 + 2), 4210752);

		RenderHelper.enableGUIStandardItemLighting();
		for (int y = 0; y < 4; y++)
		{
			for (int x = 0; x < 3; x++)
			{
				VendingMachineItem item = this.vendingMachine.getItem(x + y * 3);
				ItemStack stack = item.getStock();
				if (!stack.isEmpty())
				{
					int xPos = 40 + x * 18;
					int yPos = 18 + y * 22;
					if (x + y * 3 == this.vendingMachineInventory.getSelectedItem())
					{
						Gui.drawRect(xPos, yPos, xPos + 16, yPos + 16, HIGHLIGHT_COLOR);
					}

					this.mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, xPos, yPos);
					if (!item.isValidCurrency(this.vendingMachineInventory.getCurrencyStack(item.getPrice().getItem())) || InventoryUtils.isFull(this.vendingMachine.getInternalInventory()))
					{
						GlStateManager.disableDepthTest();
						this.mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
						this.drawTexturedModalRect(xPos - 1, yPos - 1, 176, 0, 18, 18);
					}
					this.mc.getItemRenderer().renderItemOverlays(this.mc.fontRenderer, stack, xPos, yPos);
				}
			}
		}
		RenderHelper.disableStandardItemLighting();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
	{
		int hoveredIndex = this.getHoveredIndex(mouseX, mouseY);
		if (hoveredIndex != -1 && !this.vendingMachine.getItem(hoveredIndex).getStock().isEmpty() && !InventoryUtils.isEmpty(this.vendingMachine.getInternalInventory()))
		{
			this.vendingMachineInventory.setSelectedItem(hoveredIndex);
			NetworkHandler.INSTANCE.sendToServer(new MessageSetVendingItem(hoveredIndex));
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	private int getHoveredIndex(double mouseX, double mouseY)
	{
		int hoveredIndex = -1;
		if (mouseX >= this.guiLeft + 39 && mouseX < this.guiLeft + 39 + 54 && mouseY >= this.guiTop + 17 && mouseY < this.guiTop + 17 + 84)
		{
			double slotY = ((double) mouseY - (double) (this.guiTop + 17)) / 22.0;
			if (slotY - (int) slotY < 18.0 / 22.0)
			{
				hoveredIndex = (((int) mouseX - (this.guiLeft + 39)) / 18) + (int) slotY * 3;
			}
		}
		return hoveredIndex;
	}

	@Nullable
	private List<String> getItemToolTip(int index)
	{
		VendingMachineItem item = this.vendingMachine.getItem(index);
		ItemStack stock = item.getStock();
		ItemStack price = item.getPrice();
		if (!stock.isEmpty())
		{
			List<String> tooltip = super.getItemToolTip(stock);
			tooltip.add("");
			if (InventoryUtils.isFull(this.vendingMachine.getInternalInventory()))
			{
				tooltip.add(TextFormatting.RED + I18n.format("gui." + VendingMachineMod.MOD_ID + ".vending_machine.full", this.vendingMachineInventory.getName().getFormattedText()));
			}
			else
			{
				tooltip.add(TextFormatting.GRAY + (price.isEmpty() ? I18n.format("gui." + VendingMachineMod.MOD_ID + ".vending_machine.free") : I18n.format("gui." + VendingMachineMod.MOD_ID + ".vending_machine.cost", price.getCount(), price.getDisplayName().getString())));
				if (!item.isValidCurrency(this.vendingMachineInventory.getCurrencyStack(item.getPrice().getItem())))
				{
					tooltip.add(TextFormatting.RED + I18n.format("gui." + VendingMachineMod.MOD_ID + ".vending_machine.expensive", item.getPrice().getCount() - this.vendingMachineInventory.getStackInSlot(1).getCount(), item.getPrice().getDisplayName().getString()));
				}
			}
			return tooltip;
		}
		return null;
	}
}