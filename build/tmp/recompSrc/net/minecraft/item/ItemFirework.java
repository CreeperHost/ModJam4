package net.minecraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemFirework extends Item
{
    private static final String __OBFID = "CL_00000031";

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        if (!par3World.isRemote)
        {
            EntityFireworkRocket entityfireworkrocket = new EntityFireworkRocket(par3World, (double)((float)par4 + par8), (double)((float)par5 + par9), (double)((float)par6 + par10), par1ItemStack);
            par3World.spawnEntityInWorld(entityfireworkrocket);

            if (!par2EntityPlayer.capabilities.isCreativeMode)
            {
                --par1ItemStack.stackSize;
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        if (par1ItemStack.hasTagCompound())
        {
            NBTTagCompound nbttagcompound = par1ItemStack.getTagCompound().getCompoundTag("Fireworks");

            if (nbttagcompound != null)
            {
                if (nbttagcompound.hasKey("Flight", 99))
                {
                    par3List.add(StatCollector.translateToLocal("item.fireworks.flight") + " " + nbttagcompound.getByte("Flight"));
                }

                NBTTagList nbttaglist = nbttagcompound.getTagList("Explosions", 10);

                if (nbttaglist != null && nbttaglist.tagCount() > 0)
                {
                    for (int i = 0; i < nbttaglist.tagCount(); ++i)
                    {
                        NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
                        ArrayList arraylist = new ArrayList();
                        ItemFireworkCharge.func_150902_a(nbttagcompound1, arraylist);

                        if (arraylist.size() > 0)
                        {
                            for (int j = 1; j < arraylist.size(); ++j)
                            {
                                arraylist.set(j, "  " + (String)arraylist.get(j));
                            }

                            par3List.addAll(arraylist);
                        }
                    }
                }
            }
        }
    }
}