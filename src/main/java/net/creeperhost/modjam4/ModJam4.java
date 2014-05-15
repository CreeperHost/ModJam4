package net.creeperhost.modjam4;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.creeperhost.modjam4.item.ModItems;
import net.creeperhost.modjam4.reference.ModInfo;
import net.creeperhost.modjam4.sphinx.SphinxTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION)
public class ModJam4 {
	public static final Logger logger = LogManager.getLogger(ModInfo.NAME);

    @EventHandler
    @SuppressWarnings("unused")
    public void preinit(FMLPreInitializationEvent event) {
	    ModItems.init();
    }

	@EventHandler
	@SuppressWarnings("unused")
    public void init(FMLInitializationEvent event) {
		ModItems.registerRecipes();
    }

	@EventHandler
	@SuppressWarnings("unused")
	public void postinit(FMLPostInitializationEvent event) {
		logger.info("Welcome to your mod!");
        SphinxTest.test();
	}
}
