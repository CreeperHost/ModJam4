package net.minecraft.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityLeashKnot extends EntityHanging
{
    private static final String __OBFID = "CL_00001548";

    public EntityLeashKnot(World par1World)
    {
        super(par1World);
    }

    public EntityLeashKnot(World par1World, int par2, int par3, int par4)
    {
        super(par1World, par2, par3, par4, 0);
        this.setPosition((double)par2 + 0.5D, (double)par3 + 0.5D, (double)par4 + 0.5D);
    }

    protected void entityInit()
    {
        super.entityInit();
    }

    public void setDirection(int par1) {}

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
        return par1 < 1024.0D;
    }

    /**
     * Called when this entity is broken. Entity parameter may be null.
     */
    public void onBroken(Entity par1Entity) {}

    /**
     * Either write this entity to the NBT tag given and return true, or return false without doing anything. If this
     * returns false the entity is not saved on disk. Ridden entities return false here as they are saved with their
     * rider.
     */
    public boolean writeToNBTOptional(NBTTagCompound par1NBTTagCompound)
    {
        return false;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {}

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {}

    /**
     * First layer of player interaction
     */
    public boolean interactFirst(EntityPlayer par1EntityPlayer)
    {
        ItemStack itemstack = par1EntityPlayer.getHeldItem();
        boolean flag = false;
        double d0;
        List list;
        Iterator iterator;
        EntityLiving entityliving;

        if (itemstack != null && itemstack.getItem() == Items.lead && !this.worldObj.isRemote)
        {
            d0 = 7.0D;
            list = this.worldObj.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getAABBPool().getAABB(this.posX - d0, this.posY - d0, this.posZ - d0, this.posX + d0, this.posY + d0, this.posZ + d0));

            if (list != null)
            {
                iterator = list.iterator();

                while (iterator.hasNext())
                {
                    entityliving = (EntityLiving)iterator.next();

                    if (entityliving.getLeashed() && entityliving.getLeashedToEntity() == par1EntityPlayer)
                    {
                        entityliving.setLeashedToEntity(this, true);
                        flag = true;
                    }
                }
            }
        }

        if (!this.worldObj.isRemote && !flag)
        {
            this.setDead();

            if (par1EntityPlayer.capabilities.isCreativeMode)
            {
                d0 = 7.0D;
                list = this.worldObj.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getAABBPool().getAABB(this.posX - d0, this.posY - d0, this.posZ - d0, this.posX + d0, this.posY + d0, this.posZ + d0));

                if (list != null)
                {
                    iterator = list.iterator();

                    while (iterator.hasNext())
                    {
                        entityliving = (EntityLiving)iterator.next();

                        if (entityliving.getLeashed() && entityliving.getLeashedToEntity() == this)
                        {
                            entityliving.clearLeashed(true, false);
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * checks to make sure painting can be placed there
     */
    public boolean onValidSurface()
    {
        return this.worldObj.getBlock(this.field_146063_b, this.field_146064_c, this.field_146062_d).getRenderType() == 11;
    }

    public static EntityLeashKnot func_110129_a(World par0World, int par1, int par2, int par3)
    {
        EntityLeashKnot entityleashknot = new EntityLeashKnot(par0World, par1, par2, par3);
        entityleashknot.forceSpawn = true;
        par0World.spawnEntityInWorld(entityleashknot);
        return entityleashknot;
    }

    public static EntityLeashKnot getKnotForBlock(World par0World, int par1, int par2, int par3)
    {
        List list = par0World.getEntitiesWithinAABB(EntityLeashKnot.class, AxisAlignedBB.getAABBPool().getAABB((double)par1 - 1.0D, (double)par2 - 1.0D, (double)par3 - 1.0D, (double)par1 + 1.0D, (double)par2 + 1.0D, (double)par3 + 1.0D));

        if (list != null)
        {
            Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
                EntityLeashKnot entityleashknot = (EntityLeashKnot)iterator.next();

                if (entityleashknot.field_146063_b == par1 && entityleashknot.field_146064_c == par2 && entityleashknot.field_146062_d == par3)
                {
                    return entityleashknot;
                }
            }
        }

        return null;
    }
}