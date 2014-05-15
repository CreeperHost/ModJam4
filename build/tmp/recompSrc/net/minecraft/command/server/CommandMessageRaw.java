package net.minecraft.command.server;

import com.google.gson.JsonParseException;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class CommandMessageRaw extends CommandBase
{
    private static final String __OBFID = "CL_00000667";

    public String getCommandName()
    {
        return "tellraw";
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
        return "commands.tellraw.usage";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length < 2)
        {
            throw new WrongUsageException("commands.tellraw.usage", new Object[0]);
        }
        else
        {
            EntityPlayerMP entityplayermp = getPlayer(par1ICommandSender, par2ArrayOfStr[0]);
            String s = func_82360_a(par1ICommandSender, par2ArrayOfStr, 1);

            try
            {
                IChatComponent ichatcomponent = IChatComponent.Serializer.func_150699_a(s);
                entityplayermp.addChatMessage(ichatcomponent);
            }
            catch (JsonParseException jsonparseexception)
            {
                throw new SyntaxErrorException("commands.tellraw.jsonException", new Object[] {ExceptionUtils.getRootCause(jsonparseexception).getMessage()});
            }
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getAllUsernames()) : null;
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2)
    {
        return par2 == 0;
    }
}