package com.ocelot.network;

import java.util.function.Supplier;

import com.ocelot.tileentity.TileEntityVendingMachine;
import com.ocelot.vending.VendingMachine;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageSetVendingItemPrice
{
	private BlockPos pos;
	private int index;
	private ItemStack price;

	public MessageSetVendingItemPrice(BlockPos pos, int index, ItemStack price)
	{
		this.pos = pos;
		this.index = index;
		this.price = price;
	}

	public static void encode(MessageSetVendingItemPrice msg, PacketBuffer buf)
	{
		buf.writeBlockPos(msg.pos);
		buf.writeVarInt(msg.index);
		buf.writeItemStack(msg.price);
	}

	public static MessageSetVendingItemPrice decode(PacketBuffer buf)
	{
		return new MessageSetVendingItemPrice(buf.readBlockPos(), buf.readVarInt(), buf.readItemStack());
	}

	public static void handle(MessageSetVendingItemPrice msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			EntityPlayerMP player = ctx.get().getSender();
			World world = player.world;
			if (world.getTileEntity(msg.pos) instanceof TileEntityVendingMachine)
			{
				VendingMachine vendingMachine = ((TileEntityVendingMachine) world.getTileEntity(msg.pos)).getVendingMachine();
				if (vendingMachine.getOwner() != null && player.getUniqueID().equals(vendingMachine.getOwner()))
				{
					vendingMachine.setPrice(msg.price, msg.index);
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}