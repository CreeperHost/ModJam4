package net.creeperhost.harken.handler;

import net.creeperhost.harken.reference.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SoundHandler {

    private static Queue<ISound> queue = new ConcurrentLinkedQueue<ISound>();
    private static ISound currentlyPlayingSound;

    private static Minecraft mc = Minecraft.getMinecraft();

	public static void onEntityPlay(String sound, float volume, float pitch) {
        queue.add(PositionedSoundRecord.func_147674_a(new ResourceLocation(ModInfo.ID + ":" + sound), volume));
	}

    public static void soundTick()
    {
        if (currentlyPlayingSound == null || !mc.getSoundHandler().isSoundPlaying(currentlyPlayingSound))
        {
            currentlyPlayingSound = queue.poll();
            if (currentlyPlayingSound != null)
                mc.getSoundHandler().playSound(currentlyPlayingSound);
        }
    }
}
