package net.minecraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.command.IEntitySelector;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemArmor extends Item
{
    /** Holds the 'base' maxDamage that each armorType have. */
    private static final int[] maxDamageArray = new int[] {11, 16, 15, 13};
    private static final String[] CLOTH_OVERLAY_NAMES = new String[] {"leather_helmet_overlay", "leather_chestplate_overlay", "leather_leggings_overlay", "leather_boots_overlay"};
    public static final String[] EMPTY_SLOT_NAMES = new String[] {"empty_armor_slot_helmet", "empty_armor_slot_chestplate", "empty_armor_slot_leggings", "empty_armor_slot_boots"};
    private static final IBehaviorDispenseItem dispenserBehavior = new BehaviorDefaultDispenseItem()
    {
        private static final String __OBFID = "CL_00001767";
        /**
         * Dispense the specified stack, play the dispense sound and spawn particles.
         */
        protected ItemStack dispenseStack(IBlockSource par1IBlockSource, ItemStack par2ItemStack)
        {
            EnumFacing enumfacing = BlockDispenser.func_149937_b(par1IBlockSource.getBlockMetadata());
            int i = par1IBlockSource.getXInt() + enumfacing.getFrontOffsetX();
            int j = par1IBlockSource.getYInt() + enumfacing.getFrontOffsetY();
            int k = par1IBlockSource.getZInt() + enumfacing.getFrontOffsetZ();
            AxisAlignedBB axisalignedbb = AxisAlignedBB.getAABBPool().getAABB((double)i, (double)j, (double)k, (double)(i + 1), (double)(j + 1), (double)(k + 1));
            List list = par1IBlockSource.getWorld().selectEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb, new IEntitySelector.ArmoredMob(par2ItemStack));

            if (list.size() > 0)
            {
                EntityLivingBase entitylivingbase = (EntityLivingBase)list.get(0);
                int l = entitylivingbase instanceof EntityPlayer ? 1 : 0;
                int i1 = EntityLiving.getArmorPosition(par2ItemStack);
                ItemStack itemstack1 = par2ItemStack.copy();
                itemstack1.stackSize = 1;
                entitylivingbase.setCurrentItemOrArmor(i1 - l, itemstack1);

                if (entitylivingbase instanceof EntityLiving)
                {
                    ((EntityLiving)entitylivingbase).setEquipmentDropChance(i1, 2.0F);
                }

                --par2ItemStack.stackSize;
                return par2ItemStack;
            }
            else
            {
                return super.dispenseStack(par1IBlockSource, par2ItemStack);
            }
        }
    };
    /**
     * Stores the armor type: 0 is helmet, 1 is plate, 2 is legs and 3 is boots
     */
    public final int armorType;
    /** Holds the amount of damage that the armor reduces at full durability. */
    public final int damageReduceAmount;
    /**
     * Used on RenderPlayer to select the correspondent armor to be rendered on the player: 0 is cloth, 1 is chain, 2 is
     * iron, 3 is diamond and 4 is gold.
     */
    public final int renderIndex;
    /** The EnumArmorMaterial used for this ItemArmor */
    private final ItemArmor.ArmorMaterial material;
    @SideOnly(Side.CLIENT)
    private IIcon overlayIcon;
    @SideOnly(Side.CLIENT)
    private IIcon emptySlotIcon;
    private static final String __OBFID = "CL_00001766";

    public ItemArmor(ItemArmor.ArmorMaterial p_i45325_1_, int p_i45325_2_, int p_i45325_3_)
    {
        this.material = p_i45325_1_;
        this.armorType = p_i45325_3_;
        this.renderIndex = p_i45325_2_;
        this.damageReduceAmount = p_i45325_1_.getDamageReductionAmount(p_i45325_3_);
        this.setMaxDamage(p_i45325_1_.getDurability(p_i45325_3_));
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabCombat);
        BlockDispenser.dispenseBehaviorRegistry.putObject(this, dispenserBehavior);
    }

    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack par1ItemStack, int par2)
    {
        if (par2 > 0)
        {
            return 16777215;
        }
        else
        {
            int j = this.getColor(par1ItemStack);

            if (j < 0)
            {
                j = 16777215;
            }

            return j;
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return this.material == ItemArmor.ArmorMaterial.CLOTH;
    }

    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    public int getItemEnchantability()
    {
        return this.material.getEnchantability();
    }

    /**
     * Return the armor material for this armor item.
     */
    public ItemArmor.ArmorMaterial getArmorMaterial()
    {
        return this.material;
    }

    /**
     * Return whether the specified armor ItemStack has a color.
     */
    public boolean hasColor(ItemStack par1ItemStack)
    {
        return this.material != ItemArmor.ArmorMaterial.CLOTH ? false : (!par1ItemStack.hasTagCompound() ? false : (!par1ItemStack.getTagCompound().hasKey("display", 10) ? false : par1ItemStack.getTagCompound().getCompoundTag("display").hasKey("color", 3)));
    }

    /**
     * Return the color for the specified armor ItemStack.
     */
    public int getColor(ItemStack par1ItemStack)
    {
        if (this.material != ItemArmor.ArmorMaterial.CLOTH)
        {
            return -1;
        }
        else
        {
            NBTTagCompound nbttagcompound = par1ItemStack.getTagCompound();

            if (nbttagcompound == null)
            {
                return 10511680;
            }
            else
            {
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");
                return nbttagcompound1 == null ? 10511680 : (nbttagcompound1.hasKey("color", 3) ? nbttagcompound1.getInteger("color") : 10511680);
            }
        }
    }

    /**
     * Gets an icon index based on an item's damage value and the given render pass
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int par1, int par2)
    {
        return par2 == 1 ? this.overlayIcon : super.getIconFromDamageForRenderPass(par1, par2);
    }

    /**
     * Remove the color from the specified armor ItemStack.
     */
    public void removeColor(ItemStack par1ItemStack)
    {
        if (this.material == ItemArmor.ArmorMaterial.CLOTH)
        {
            NBTTagCompound nbttagcompound = par1ItemStack.getTagCompound();

            if (nbttagcompound != null)
            {
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");

                if (nbttagcompound1.hasKey("color"))
                {
                    nbttagcompound1.removeTag("color");
                }
            }
        }
    }

    public void func_82813_b(ItemStack par1ItemStack, int par2)
    {
        if (this.material != ItemArmor.ArmorMaterial.CLOTH)
        {
            throw new UnsupportedOperationException("Can\'t dye non-leather!");
        }
        else
        {
            NBTTagCompound nbttagcompound = par1ItemStack.getTagCompound();

            if (nbttagcompound == null)
            {
                nbttagcompound = new NBTTagCompound();
                par1ItemStack.setTagCompound(nbttagcompound);
            }

            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");

            if (!nbttagcompound.hasKey("display", 10))
            {
                nbttagcompound.setTag("display", nbttagcompound1);
            }

            nbttagcompound1.setInteger("color", par2);
        }
    }

    /**
     * Return whether this item is repairable in an anvil.
     */
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
        return this.material.func_151685_b() == par2ItemStack.getItem() ? true : super.getIsRepairable(par1ItemStack, par2ItemStack);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister)
    {
        super.registerIcons(par1IconRegister);

        if (this.material == ItemArmor.ArmorMaterial.CLOTH)
        {
            this.overlayIcon = par1IconRegister.registerIcon(CLOTH_OVERLAY_NAMES[this.armorType]);
        }

        this.emptySlotIcon = par1IconRegister.registerIcon(EMPTY_SLOT_NAMES[this.armorType]);
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        int i = EntityLiving.getArmorPosition(par1ItemStack) - 1;
        ItemStack itemstack1 = par3EntityPlayer.getCurrentArmor(i);

        if (itemstack1 == null)
        {
            par3EntityPlayer.setCurrentItemOrArmor(i + 1, par1ItemStack.copy());  //Forge: Vanilla bug fix associated with fixed setCurrentItemOrArmor indexs for players.
            par1ItemStack.stackSize = 0;
        }

        return par1ItemStack;
    }

    @SideOnly(Side.CLIENT)
    public static IIcon func_94602_b(int par0)
    {
        switch (par0)
        {
            case 0:
                return Items.diamond_helmet.emptySlotIcon;
            case 1:
                return Items.diamond_chestplate.emptySlotIcon;
            case 2:
                return Items.diamond_leggings.emptySlotIcon;
            case 3:
                return Items.diamond_boots.emptySlotIcon;
            default:
                return null;
        }
    }

    public static enum ArmorMaterial
    {
        CLOTH(5, new int[]{1, 3, 2, 1}, 15),
        CHAIN(15, new int[]{2, 5, 4, 1}, 12),
        IRON(15, new int[]{2, 6, 5, 2}, 9),
        GOLD(7, new int[]{2, 5, 3, 1}, 25),
        DIAMOND(33, new int[]{3, 8, 6, 3}, 10);
        /**
         * Holds the maximum damage factor (each piece multiply this by it's own value) of the material, this is the
         * item damage (how much can absorb before breaks)
         */
        private int maxDamageFactor;
        /**
         * Holds the damage reduction (each 1 points is half a shield on gui) of each piece of armor (helmet, plate,
         * legs and boots)
         */
        private int[] damageReductionAmountArray;
        /** Return the enchantability factor of the material */
        private int enchantability;

        private static final String __OBFID = "CL_00001768";

        //Added by forge for custom Armor materials.
        public Item customCraftingMaterial = null;

        private ArmorMaterial(int par3, int[] par4ArrayOfInteger, int par5)
        {
            this.maxDamageFactor = par3;
            this.damageReductionAmountArray = par4ArrayOfInteger;
            this.enchantability = par5;
        }

        /**
         * Returns the durability for a armor slot of for this type.
         */
        public int getDurability(int par1)
        {
            return ItemArmor.maxDamageArray[par1] * this.maxDamageFactor;
        }

        /**
         * Return the damage reduction (each 1 point is a half a shield on gui) of the piece index passed (0 = helmet, 1
         * = plate, 2 = legs and 3 = boots)
         */
        public int getDamageReductionAmount(int par1)
        {
            return this.damageReductionAmountArray[par1];
        }

        /**
         * Return the enchantability factor of the material.
         */
        public int getEnchantability()
        {
            return this.enchantability;
        }

        public Item func_151685_b()
        {
            switch (this)
            {
                case CLOTH:   return Items.leather;
                case CHAIN:   return Items.iron_ingot;
                case GOLD:    return Items.gold_ingot;
                case IRON:    return Items.iron_ingot;
                case DIAMOND: return Items.diamond;
                default:      return customCraftingMaterial;
            }
        }
    }
}