package net.minecraft.entity.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class EntityItemFrame extends EntityHanging
{
    /** Chance for this item frame's item to drop from the frame. */
    private float itemDropChance = 1.0F;
    private static final String __OBFID = "CL_00001547";

    public EntityItemFrame(World par1World)
    {
        super(par1World);
    }

    public EntityItemFrame(World par1World, int par2, int par3, int par4, int par5)
    {
        super(par1World, par2, par3, par4, par5);
        this.setDirection(par5);
    }

    protected void entityInit()
    {
        this.getDataWatcher().addObjectByDataType(2, 5);
        this.getDataWatcher().addObject(3, Byte.valueOf((byte)0));
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
    {
        if (this.isEntityInvulnerable())
        {
            return false;
        }
        else if (this.getDisplayedItem() != null)
        {
            if (!this.worldObj.isRemote)
            {
                this.func_146065_b(par1DamageSource.getEntity(), false);
                this.setDisplayedItem((ItemStack)null);
            }

            return true;
        }
        else
        {
            return super.attackEntityFrom(par1DamageSource, par2);
        }
    }

    public int getWidthPixels()
    {
        return 9;
    }

    public int getHeightPixels()
    {
        return 9;
    }

    /**
     * Checks if the entity is in range to render by using the past in distance and comparing it to its average edge
     * length * 64 * renderDistanceWeight Args: distance
     */
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double par1)
    {
        double d1 = 16.0D;
        d1 *= 64.0D * this.renderDistanceWeight;
        return par1 < d1 * d1;
    }

    /**
     * Called when this entity is broken. Entity parameter may be null.
     */
    public void onBroken(Entity par1Entity)
    {
        this.func_146065_b(par1Entity, true);
    }

    public void func_146065_b(Entity p_146065_1_, boolean p_146065_2_)
    {
        ItemStack itemstack = this.getDisplayedItem();

        if (p_146065_1_ instanceof EntityPlayer)
        {
            EntityPlayer entityplayer = (EntityPlayer)p_146065_1_;

            if (entityplayer.capabilities.isCreativeMode)
            {
                this.removeFrameFromMap(itemstack);
                return;
            }
        }

        if (p_146065_2_)
        {
            this.entityDropItem(new ItemStack(Items.item_frame), 0.0F);
        }

        if (itemstack != null && this.rand.nextFloat() < this.itemDropChance)
        {
            itemstack = itemstack.copy();
            this.removeFrameFromMap(itemstack);
            this.entityDropItem(itemstack, 0.0F);
        }
    }

    /**
     * Removes the dot representing this frame's position from the map when the item frame is broken.
     */
    private void removeFrameFromMap(ItemStack par1ItemStack)
    {
        if (par1ItemStack != null)
        {
            if (par1ItemStack.getItem() == Items.filled_map)
            {
                MapData mapdata = ((ItemMap)par1ItemStack.getItem()).getMapData(par1ItemStack, this.worldObj);
                mapdata.playersVisibleOnMap.remove("frame-" + this.getEntityId());
            }

            par1ItemStack.setItemFrame((EntityItemFrame)null);
        }
    }

    public ItemStack getDisplayedItem()
    {
        return this.getDataWatcher().getWatchableObjectItemStack(2);
    }

    public void setDisplayedItem(ItemStack par1ItemStack)
    {
        if (par1ItemStack != null)
        {
            par1ItemStack = par1ItemStack.copy();
            par1ItemStack.stackSize = 1;
            par1ItemStack.setItemFrame(this);
        }

        this.getDataWatcher().updateObject(2, par1ItemStack);
        this.getDataWatcher().setObjectWatched(2);
    }

    /**
     * Return the rotation of the item currently on this frame.
     */
    public int getRotation()
    {
        return this.getDataWatcher().getWatchableObjectByte(3);
    }

    public void setItemRotation(int par1)
    {
        this.getDataWatcher().updateObject(3, Byte.valueOf((byte)(par1 % 4)));
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        if (this.getDisplayedItem() != null)
        {
            par1NBTTagCompound.setTag("Item", this.getDisplayedItem().writeToNBT(new NBTTagCompound()));
            par1NBTTagCompound.setByte("ItemRotation", (byte)this.getRotation());
            par1NBTTagCompound.setFloat("ItemDropChance", this.itemDropChance);
        }

        super.writeEntityToNBT(par1NBTTagCompound);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        NBTTagCompound nbttagcompound1 = par1NBTTagCompound.getCompoundTag("Item");

        if (nbttagcompound1 != null && !nbttagcompound1.hasNoTags())
        {
            this.setDisplayedItem(ItemStack.loadItemStackFromNBT(nbttagcompound1));
            this.setItemRotation(par1NBTTagCompound.getByte("ItemRotation"));

            if (par1NBTTagCompound.hasKey("ItemDropChance", 99))
            {
                this.itemDropChance = par1NBTTagCompound.getFloat("ItemDropChance");
            }
        }

        super.readEntityFromNBT(par1NBTTagCompound);
    }

    /**
     * First layer of player interaction
     */
    public boolean interactFirst(EntityPlayer par1EntityPlayer)
    {
        if (this.getDisplayedItem() == null)
        {
            ItemStack itemstack = par1EntityPlayer.getHeldItem();

            if (itemstack != null && !this.worldObj.isRemote)
            {
                this.setDisplayedItem(itemstack);

                if (!par1EntityPlayer.capabilities.isCreativeMode && --itemstack.stackSize <= 0)
                {
                    par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, (ItemStack)null);
                }
            }
        }
        else if (!this.worldObj.isRemote)
        {
            this.setItemRotation(this.getRotation() + 1);
        }

        return true;
    }
}