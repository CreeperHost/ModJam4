package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

public class CommandGive extends CommandBase
{
    private static final String __OBFID = "CL_00000502";

    public String getCommandName()
    {
        return "give";
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
        return "commands.give.usage";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length < 2)
        {
            throw new WrongUsageException("commands.give.usage", new Object[0]);
        }
        else
        {
            EntityPlayerMP entityplayermp = getPlayer(par1ICommandSender, par2ArrayOfStr[0]);
            Item item = getItemByText(par1ICommandSender, par2ArrayOfStr[1]);
            int i = 1;
            int j = 0;

            if (par2ArrayOfStr.length >= 3)
            {
                i = parseIntBounded(par1ICommandSender, par2ArrayOfStr[2], 1, 64);
            }

            if (par2ArrayOfStr.length >= 4)
            {
                j = parseInt(par1ICommandSender, par2ArrayOfStr[3]);
            }

            ItemStack itemstack = new ItemStack(item, i, j);

            if (par2ArrayOfStr.length >= 5)
            {
                String s = func_147178_a(par1ICommandSender, par2ArrayOfStr, 4).getUnformattedText();

                try
                {
                    NBTBase nbtbase = JsonToNBT.func_150315_a(s);

                    if (!(nbtbase instanceof NBTTagCompound))
                    {
                        notifyAdmins(par1ICommandSender, "commands.give.tagError", new Object[] {"Not a valid tag"});
                        return;
                    }

                    itemstack.setTagCompound((NBTTagCompound)nbtbase);
                }
                catch (NBTException nbtexception)
                {
                    notifyAdmins(par1ICommandSender, "commands.give.tagError", new Object[] {nbtexception.getMessage()});
                    return;
                }
            }

            EntityItem entityitem = entityplayermp.dropPlayerItemWithRandomChoice(itemstack, false);
            entityitem.delayBeforeCanPickup = 0;
            entityitem.func_145797_a(entityplayermp.getCommandSenderName());
            notifyAdmins(par1ICommandSender, "commands.give.success", new Object[] {itemstack.func_151000_E(), Integer.valueOf(i), entityplayermp.getCommandSenderName()});
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, this.getPlayers()) : (par2ArrayOfStr.length == 2 ? getListOfStringsFromIterableMatchingLastWord(par2ArrayOfStr, Item.itemRegistry.getKeys()) : null);
    }

    protected String[] getPlayers()
    {
        return MinecraftServer.getServer().getAllUsernames();
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2)
    {
        return par2 == 0;
    }
}