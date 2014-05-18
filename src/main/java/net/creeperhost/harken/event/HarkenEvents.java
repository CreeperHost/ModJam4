package net.creeperhost.harken.event;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.creeperhost.harken.Harken;
import net.creeperhost.harken.MCBridge.MCInformation;
import net.creeperhost.harken.handler.SoundHandler;
import net.creeperhost.harken.item.ModItems;
import net.creeperhost.harken.reference.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public class HarkenEvents {
	@SubscribeEvent
	public void onGlassesInteractEvent(PlayerInteractEvent event) {
		if (isServer(event.entityPlayer.getEntityWorld())) {
			if (isWearingGlasses(event.entityPlayer)) {
				if (actionWasRightClick(event.action)) {
					Harken.logger.info("I right clicked while wearing glasses!");
					SoundHandler.onEntityPlay("herobrine.insult.human", 1, 1);
				}
			}
		}
	}

    private int tickCount;

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onClientTickEvent(TickEvent.ClientTickEvent clientTickEvent)
    {
        if(clientTickEvent.phase != TickEvent.Phase.END) return;

        if ((++tickCount % 20) == 0)
        {
            // stuff to do once every second
            MCInformation.gatherInformation();
        }

        //other stuff
    }

	private boolean isServer(World world) {
		return !world.isRemote;
	}

	private boolean isWearingGlasses(EntityPlayer player) {
		return (player.getCurrentArmor(3) != null) && (player.getCurrentArmor(3).getItem() == ModItems.glasses);
	}

	private boolean actionWasRightClick(Action action) {
		return (action == Action.RIGHT_CLICK_AIR) || (action == Action.RIGHT_CLICK_BLOCK);
	}
}

