package com.ocelot.tileentity.render;

import org.lwjgl.opengl.GL11;

import com.ocelot.init.ModItems;
import com.ocelot.tileentity.TileEntityVendingMachine;
import com.ocelot.vending.VendingMachine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.IChestLid;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RenderVendingMachine extends TileEntityRenderer<TileEntityVendingMachine>
{
	private static final ItemStack WINDOW = new ItemStack(ModItems.VENDING_MACHINE_WINDOW);

	@Override
	public void render(TileEntityVendingMachine te, double x, double y, double z, float partialTicks, int destroyStage)
	{
		World world = te.getWorld();
		BlockPos pos = te.getPos();
		if (world != null)
		{
			if (destroyStage >= 0)
			{
				GlStateManager.matrixMode(5890);
				GlStateManager.pushMatrix();
				GlStateManager.scalef(8.0F, 4.0F, 1.0F);
				GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
				GlStateManager.matrixMode(5888);
			}
			else
			{
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			}

			EnumFacing facing = EnumFacing.NORTH;
			if (world.getBlockState(pos).has(BlockStateProperties.HORIZONTAL_FACING))
			{
				facing = world.getBlockState(pos).get(BlockStateProperties.HORIZONTAL_FACING);
			}

			GlStateManager.pushMatrix();
			GlStateManager.enableLighting();
			GlStateManager.translated(x + 0.5, y, z + 0.5);
			GlStateManager.rotatef(-facing.getOpposite().getHorizontalAngle(), 0, 1, 0);
			GlStateManager.translated(-0.5, 0, -0.5);

			GlStateManager.pushMatrix();

			float f = ((IChestLid) te).getLidAngle(partialTicks);
			f = 1.0F - f;
			f = 1.0F - f * f * f;
			float angle = -(float) (f * (Math.PI / 2.0));

			GlStateManager.translated(14.5 * 0.0625, 20 * 0.0625, 3 * 0.0625);
			GlStateManager.rotatef((float) Math.toDegrees(angle), 0, 1, 0);
			Minecraft.getInstance().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			Minecraft.getInstance().getItemRenderer().renderItem(WINDOW, Minecraft.getInstance().getItemRenderer().getModelWithOverrides(WINDOW));

			GlStateManager.popMatrix();

			if (destroyStage >= 0)
			{
				GlStateManager.matrixMode(5890);
				GlStateManager.popMatrix();
				GlStateManager.matrixMode(5888);
			}

			VendingMachine vendingMachine = te.getVendingMachine();
			GlStateManager.translated(0.0625 * 13, 28 * 0.0625, 0.0625 * 6);
			GlStateManager.scaled(0.0625 * 2, 0.0625 * 2, 0.0625 * 2);

			for (int slotY = 0; slotY < 4; slotY++)
			{
				GlStateManager.pushMatrix();
				GlStateManager.translated(0, -slotY * 2.5, 0);
				for (int slotX = 0; slotX < 3; slotX++)
				{
					ItemStack stock = vendingMachine.getItem(slotX + slotY * 3).getStock();
					if (!stock.isEmpty())
					{
						RenderHelper.enableStandardItemLighting();
						GlStateManager.enableRescaleNormal();
						GlStateManager.pushMatrix();
						GlStateManager.rotatef((Minecraft.getInstance().player.ticksExisted + partialTicks) * 2, 0, 1, 0);
						Minecraft.getInstance().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
						Minecraft.getInstance().getItemRenderer().renderItem(stock, Minecraft.getInstance().getItemRenderer().getModelWithOverrides(stock));
						GlStateManager.popMatrix();
						GlStateManager.disableRescaleNormal();
						RenderHelper.disableStandardItemLighting();

						GlStateManager.pushMatrix();
						GlStateManager.scaled(0.0625, -0.0625, 0.0625);
						GlStateManager.translatef(4, -8, -12);
						this.renderOverlays(stock, 0, 0);
						GlStateManager.popMatrix();

						GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					}
					GlStateManager.translated(-1.625, 0, 0);
				}
				GlStateManager.popMatrix();
			}

			GlStateManager.popMatrix();
		}
	}

	private void renderOverlays(ItemStack stack, float xPosition, float yPosition)
	{
		if (stack.getCount() != 1)
		{
			GlStateManager.rotatef(180, 0, 1, 0);
			String s = String.valueOf(stack.getCount());
			GlStateManager.disableLighting();
			GlStateManager.disableBlend();
			Minecraft.getInstance().fontRenderer.drawString(s, (float) (xPosition + 19 - 2 - Minecraft.getInstance().fontRenderer.getStringWidth(s)), (float) (yPosition + 6 + 3), 16777215);
			GlStateManager.translated(0, 0, -0.01);
			Minecraft.getInstance().fontRenderer.drawString(s, (float) (xPosition + 19 - 2 - Minecraft.getInstance().fontRenderer.getStringWidth(s) + 1), (float) (yPosition + 6 + 3 + 1), -12566464);
			GlStateManager.depthFunc(GL11.GL_LEQUAL);
			GlStateManager.enableBlend();
			GlStateManager.enableLighting();
		}
	}
}