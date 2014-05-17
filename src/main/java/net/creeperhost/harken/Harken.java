package net.creeperhost.harken;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.creeperhost.harken.item.ModItems;
import net.creeperhost.harken.reference.ModInfo;
import net.creeperhost.harken.voice.VoceInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.lang.Class;
import java.io.*;


@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION)
public class Harken {
	public static final Logger logger = LogManager.getLogger(ModInfo.NAME);

    @EventHandler
    @SuppressWarnings("unused")
    public void preinit(FMLPreInitializationEvent event) {
        try {
            Class.forName("voce.Utils");//Let's see if we have our libraries, hopefully gradle has this shit covered :D
        } catch( ClassNotFoundException e ) {
            //voce isn't around, DOWNLOAD ALL THE LIBS!
            downloadLib("http://www.creeperrepo.net/ci/ModJam4-CreeperHost/libs/cmulex.jar");
            downloadLib("http://www.creeperrepo.net/ci/ModJam4-CreeperHost/libs/en_us.jar");
            downloadLib("http://www.creeperrepo.net/ci/ModJam4-CreeperHost/libs/freetts.jar");
            downloadLib("http://www.creeperrepo.net/ci/ModJam4-CreeperHost/libs/jsapi.jar");
            downloadLib("http://www.creeperrepo.net/ci/ModJam4-CreeperHost/libs/sphinx4.jar");
            downloadLib("http://www.creeperrepo.net/ci/ModJam4-CreeperHost/libs/voce.jar");
            downloadLib("http://www.creeperrepo.net/ci/ModJam4-CreeperHost/libs/cmu_us_kal.jar");
            downloadLib("http://www.creeperrepo.net/ci/ModJam4-CreeperHost/libs/WSJ_8gau_13dCep_16k_40mel_130Hz_6800Hz.jar");
        }
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
        VoceInterface.init(false);
        System.out.println("Welcome to Harken, a modjam project aimed to improve accessiblity for disabled gamers.");
        VoceInterface.listen();
	}
    static final String soundPrefix = "/assets/harken/sounds/herobrine/";

    public boolean downloadLib(String url)
    {
        try {
            java.io.BufferedInputStream in = new java.io.BufferedInputStream(new java.net.URL(url).openStream());
            String[] fileSplit = url.split("/");
            java.io.FileOutputStream fos = new java.io.FileOutputStream("./mods/"+fileSplit[fileSplit.length-1]);
            java.io.BufferedOutputStream bout = new BufferedOutputStream(fos);
            byte data[] = new byte[1024];
            int read;
            while ((read = in.read(data, 0, 1024)) >= 0) {
                bout.write(data, 0, read);
            }
            bout.close();
            in.close();
        } catch (Exception e)
        {
            return false;
        }
        return true;
    }
}
