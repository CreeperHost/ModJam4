package net.creeperhost.harken.MCBridge;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;

/**
 * Created by Acer on 19/05/2014.
 */
public class CommandToggle extends CommandBase {

    public String getCommandName()
    {
        return "harkentoggle";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        MCInformation.steve = !MCInformation.steve;
        par1ICommandSender.addChatMessage(new ChatComponentText("Steve recognition now " + (MCInformation.steve == true ? "on." : "off.")));
    }
}
