package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityEnderChest;

public class InventoryEnderChest extends InventoryBasic
{
    private TileEntityEnderChest associatedChest;
    private static final String __OBFID = "CL_00001759";

    public InventoryEnderChest()
    {
        super("container.enderchest", false, 27);
    }

    public void func_146031_a(TileEntityEnderChest p_146031_1_)
    {
        this.associatedChest = p_146031_1_;
    }

    public void loadInventoryFromNBT(NBTTagList par1NBTTagList)
    {
        int i;

        for (i = 0; i < this.getSizeInventory(); ++i)
        {
            this.setInventorySlotContents(i, (ItemStack)null);
        }

        for (i = 0; i < par1NBTTagList.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = par1NBTTagList.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;

            if (j >= 0 && j < this.getSizeInventory())
            {
                this.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound));
            }
        }
    }

    public NBTTagList saveInventoryToNBT()
    {
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.getSizeInventory(); ++i)
        {
            ItemStack itemstack = this.getStackInSlot(i);

            if (itemstack != null)
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                itemstack.writeToNBT(nbttagcompound);
                nbttaglist.appendTag(nbttagcompound);
            }
        }

        return nbttaglist;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return this.associatedChest != null && !this.associatedChest.func_145971_a(par1EntityPlayer) ? false : super.isUseableByPlayer(par1EntityPlayer);
    }

    public void openInventory()
    {
        if (this.associatedChest != null)
        {
            this.associatedChest.func_145969_a();
        }

        super.openInventory();
    }

    public void closeInventory()
    {
        if (this.associatedChest != null)
        {
            this.associatedChest.func_145970_b();
        }

        super.closeInventory();
        this.associatedChest = null;
    }
}