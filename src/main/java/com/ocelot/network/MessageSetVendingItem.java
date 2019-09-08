package com.ocelot.network;

import java.util.function.Supplier;

import com.ocelot.container.ContainerVendingMachine;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageSetVendingItem
{
	private int selectedIndex;

	public MessageSetVendingItem(int selectedIndex)
	{
		this.selectedIndex = selectedIndex;
	}

	public static void encode(MessageSetVendingItem msg, PacketBuffer buf)
	{
		buf.writeVarInt(msg.selectedIndex);
	}

	public static MessageSetVendingItem decode(PacketBuffer buf)
	{
		return new MessageSetVendingItem(buf.readVarInt());
	}

	public static void handle(MessageSetVendingItem msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			EntityPlayerMP player = ctx.get().getSender();
			if (player.openContainer instanceof ContainerVendingMachine)
			{
				((ContainerVendingMachine) player.openContainer).getVendingMachineInventory().setSelectedItem(msg.selectedIndex);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}