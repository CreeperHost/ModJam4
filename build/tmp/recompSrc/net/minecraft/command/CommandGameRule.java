package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.GameRules;

public class CommandGameRule extends CommandBase
{
    private static final String __OBFID = "CL_00000475";

    public String getCommandName()
    {
        return "gamerule";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "commands.gamerule.usage";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        String s1;

        if (par2ArrayOfStr.length == 2)
        {
            s1 = par2ArrayOfStr[0];
            String s2 = par2ArrayOfStr[1];
            GameRules gamerules2 = this.getGameRules();

            if (gamerules2.hasRule(s1))
            {
                gamerules2.setOrCreateGameRule(s1, s2);
                notifyAdmins(par1ICommandSender, "commands.gamerule.success", new Object[0]);
            }
            else
            {
                notifyAdmins(par1ICommandSender, "commands.gamerule.norule", new Object[] {s1});
            }
        }
        else if (par2ArrayOfStr.length == 1)
        {
            s1 = par2ArrayOfStr[0];
            GameRules gamerules1 = this.getGameRules();

            if (gamerules1.hasRule(s1))
            {
                String s = gamerules1.getGameRuleStringValue(s1);
                par1ICommandSender.addChatMessage((new ChatComponentText(s1)).appendText(" = ").appendText(s));
            }
            else
            {
                notifyAdmins(par1ICommandSender, "commands.gamerule.norule", new Object[] {s1});
            }
        }
        else if (par2ArrayOfStr.length == 0)
        {
            GameRules gamerules = this.getGameRules();
            par1ICommandSender.addChatMessage(new ChatComponentText(joinNiceString(gamerules.getRules())));
        }
        else
        {
            throw new WrongUsageException("commands.gamerule.usage", new Object[0]);
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, this.getGameRules().getRules()) : (par2ArrayOfStr.length == 2 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] {"true", "false"}): null);
    }

    /**
     * Return the game rule set this command should be able to manipulate.
     */
    private GameRules getGameRules()
    {
        return MinecraftServer.getServer().worldServerForDimension(0).getGameRules();
    }
}