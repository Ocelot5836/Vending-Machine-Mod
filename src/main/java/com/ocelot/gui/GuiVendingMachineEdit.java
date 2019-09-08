package com.ocelot.gui;

import java.util.ArrayList;
import java.util.List;

import com.ocelot.VendingMachineMod;
import com.ocelot.container.ContainerVendingMachineEdit;
import com.ocelot.network.MessageSetVendingItemPrice;
import com.ocelot.network.NetworkHandler;
import com.ocelot.vending.VendingMachine;
import com.ocelot.vending.VendingMachineItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class GuiVendingMachineEdit extends GuiContainer
{
	public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(VendingMachineMod.MOD_ID, "textures/gui/container/vending_machine_edit.png");

	private IInventory vendingMachineInventory;
	private VendingMachine vendingMachine;

	public GuiVendingMachineEdit(EntityPlayer player, VendingMachine vendingMachine)
	{
		super(new ContainerVendingMachineEdit(player, vendingMachine));
		this.vendingMachineInventory = ((ContainerVendingMachineEdit) this.inventorySlots).getVendingMachineInventory();
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
			this.drawHoveringText(this.getItemToolTip(hoveredIndex), mouseX, mouseY);
		}
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
		int hoveredIndex = this.getHoveredIndex(mouseX, mouseY);
		for (int y = 0; y < 4; y++)
		{
			for (int x = 0; x < 3; x++)
			{
				VendingMachineItem item = this.vendingMachine.getItem(x + y * 3);
				ItemStack stack = item.getPrice();
				int xPos = 92 + x * 18;
				int yPos = 18 + y * 22;

				if (!stack.isEmpty())
				{
					this.mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, xPos, yPos);
					this.mc.getItemRenderer().renderItemOverlays(this.mc.fontRenderer, stack, xPos, yPos);
				}
				else
				{
					this.mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
					this.drawTexturedModalRect(xPos, yPos, 176, 0, 16, 16);
				}

				if (hoveredIndex == x + y * 3)
				{
					GlStateManager.disableDepthTest();
					Gui.drawRect(xPos, yPos, xPos + 16, yPos + 16, -2130706433);
					GlStateManager.color4f(1, 1, 1, 1);
					GlStateManager.enableBlend();
				}
			}
		}
		RenderHelper.disableStandardItemLighting();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
	{
		int hoveredIndex = this.getHoveredIndex(mouseX, mouseY);
		if (hoveredIndex != -1)
		{
			ItemStack price = this.vendingMachine.getItem(hoveredIndex).getPrice();
			if (price.isEmpty())
			{
				ItemStack stack = Minecraft.getInstance().player.inventory.getItemStack();
				if (!stack.isEmpty())
				{
					this.vendingMachine.setPrice(stack.copy(), hoveredIndex);
					NetworkHandler.INSTANCE.sendToServer(new MessageSetVendingItemPrice(this.vendingMachine.getTe().getPos(), hoveredIndex, stack.copy()));
				}
			}
			else
			{
				this.vendingMachine.setPrice(ItemStack.EMPTY, hoveredIndex);
				NetworkHandler.INSTANCE.sendToServer(new MessageSetVendingItemPrice(this.vendingMachine.getTe().getPos(), hoveredIndex, ItemStack.EMPTY));
			}
		}
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	private int getHoveredIndex(double mouseX, double mouseY)
	{
		int hoveredIndex = -1;
		if (mouseX >= this.guiLeft + 91 && mouseX < this.guiLeft + 91 + 54 && mouseY >= this.guiTop + 17 && mouseY < this.guiTop + 17 + 84)
		{
			double slotY = ((double) mouseY - (double) (this.guiTop + 17)) / 22.0;
			if (slotY - (int) slotY < 18.0 / 22.0)
			{
				hoveredIndex = (((int) mouseX - (this.guiLeft + 91)) / 18) + (int) slotY * 3;
			}
		}
		return hoveredIndex;
	}

	private List<String> getItemToolTip(int index)
	{
		VendingMachineItem item = this.vendingMachine.getItem(index);
		ItemStack price = item.getPrice();
		List<String> tooltip = new ArrayList<String>();
		if (price.isEmpty())
		{
			tooltip.addAll(this.mc.fontRenderer.listFormattedStringToWidth(TextFormatting.GRAY + I18n.format("gui." + VendingMachineMod.MOD_ID + ".vending_machine.edit.set"), 150));
		}
		else
		{
			tooltip.addAll(super.getItemToolTip(price));
			tooltip.add("");
			tooltip.addAll(this.mc.fontRenderer.listFormattedStringToWidth(TextFormatting.RED + I18n.format("gui." + VendingMachineMod.MOD_ID + ".vending_machine.edit.remove"), 150));
		}
		return tooltip;
	}
}