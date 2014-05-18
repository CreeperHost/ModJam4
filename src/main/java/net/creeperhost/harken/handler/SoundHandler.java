package net.creeperhost.harken.handler;

import net.creeperhost.harken.reference.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class SoundHandler {
	public static void onEntityPlay(String sound, float volume, float pitch) {
        Minecraft.getMinecraft().getSoundHandler().playDelayedSound(PositionedSoundRecord.func_147674_a(new ResourceLocation(ModInfo.ID + ":" + sound), volume), 0);
                //world.playSoundAtEntity(entity, ModInfo.ID + ":" + sound, volume, pitch);
	}
}
