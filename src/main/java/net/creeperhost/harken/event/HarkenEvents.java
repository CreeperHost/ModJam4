package net.creeperhost.harken.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.creeperhost.harken.Harken;
import net.creeperhost.harken.handler.SoundHandler;
import net.creeperhost.harken.item.ModItems;
import net.creeperhost.harken.reference.ModInfo;
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
					SoundHandler.onEntityPlay("herobrine.insult.human", event.entityPlayer.getEntityWorld(), event.entityPlayer, 1, 1);
				}
			}
		}
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
