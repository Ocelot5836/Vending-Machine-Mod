package com.ocelot.gui;

import com.ocelot.VendingMachineMod;
import com.ocelot.container.ContainerVendingMachineInternal;
import com.ocelot.vending.VendingMachine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiVendingMachineInternal extends GuiContainer
{
	public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(VendingMachineMod.MOD_ID, "textures/gui/container/vending_machine_internal.png");

	private InventoryPlayer playerInventory;

	public GuiVendingMachineInternal(EntityPlayer player, VendingMachine vendingMachine)
	{
		super(new ContainerVendingMachineInternal(player, vendingMachine));
		this.playerInventory = Minecraft.getInstance().player.inventory;
	}

	@Override
	protected void initGui()
	{
		this.xSize = 176;
		this.ySize = 166;
		super.initGui();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		super.drawDefaultBackground();
		GlStateManager.color4f(1, 1, 1, 1);
		super.render(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		this.fontRenderer.drawString(I18n.format("gui." + VendingMachineMod.MOD_ID + ".vending_machine"), 8.0F, 6.0F, 4210752);
		this.fontRenderer.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float) (this.ySize - 96 + 3), 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
	}
}