package net.minecraft.command;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface IEntitySelector
{
    IEntitySelector selectAnything = new IEntitySelector()
    {
        private static final String __OBFID = "CL_00001541";
        /**
         * Return whether the specified entity is applicable to this filter.
         */
        public boolean isEntityApplicable(Entity par1Entity)
        {
            return par1Entity.isEntityAlive();
        }
    };
    IEntitySelector selectInventories = new IEntitySelector()
    {
        private static final String __OBFID = "CL_00001542";
        /**
         * Return whether the specified entity is applicable to this filter.
         */
        public boolean isEntityApplicable(Entity par1Entity)
        {
            return par1Entity instanceof IInventory && par1Entity.isEntityAlive();
        }
    };

    /**
     * Return whether the specified entity is applicable to this filter.
     */
    boolean isEntityApplicable(Entity var1);

    public static class ArmoredMob implements IEntitySelector
        {
            private final ItemStack field_96567_c;
            private static final String __OBFID = "CL_00001543";

            public ArmoredMob(ItemStack par1ItemStack)
            {
                this.field_96567_c = par1ItemStack;
            }

            /**
             * Return whether the specified entity is applicable to this filter.
             */
            public boolean isEntityApplicable(Entity par1Entity)
            {
                if (!par1Entity.isEntityAlive())
                {
                    return false;
                }
                else if (!(par1Entity instanceof EntityLivingBase))
                {
                    return false;
                }
                else
                {
                    EntityLivingBase entitylivingbase = (EntityLivingBase)par1Entity;
                    return entitylivingbase.getEquipmentInSlot(EntityLiving.getArmorPosition(this.field_96567_c)) != null ? false : (entitylivingbase instanceof EntityLiving ? ((EntityLiving)entitylivingbase).canPickUpLoot() : entitylivingbase instanceof EntityPlayer);
                }
            }
        }
}