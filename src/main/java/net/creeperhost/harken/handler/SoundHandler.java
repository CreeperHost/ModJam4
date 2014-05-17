package net.creeperhost.harken.handler;

import net.creeperhost.harken.reference.ModInfo;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class SoundHandler {
	public static void onEntityPlay(String sound, World world, Entity entity, float volume, float pitch) {
		world.playSoundAtEntity(entity, ModInfo.ID + ":" + sound, volume, pitch);
	}
}
