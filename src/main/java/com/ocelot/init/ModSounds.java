package com.ocelot.init;

import java.util.List;

import com.google.common.collect.Lists;
import com.ocelot.VendingMachineMod;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class ModSounds
{
	private static final List<SoundEvent> SOUNDS = Lists.newArrayList();

	public static final SoundEvent BLOCK_VENDING_MACHINE_VEND;
	
	static
	{
		BLOCK_VENDING_MACHINE_VEND = new SoundEvent(new ResourceLocation(VendingMachineMod.MOD_ID, "block.vending_machine.vend"));
	}

	protected static void init()
	{

	}

	public static void registerSound(SoundEvent sound)
	{
		if (sound.getRegistryName() == null)
			throw new RuntimeException("Sound \'" + sound.getClass() + "\' is missing a registry name!");
		SOUNDS.add(sound);
	}

	public static SoundEvent[] getSounds()
	{
		return SOUNDS.toArray(new SoundEvent[0]);
	}
}