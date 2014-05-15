package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.HoverEvent;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public final class ItemStack
{
    public static final DecimalFormat field_111284_a = new DecimalFormat("#.###");
    /** Size of the stack. */
    public int stackSize;
    /**
     * Number of animation frames to go when receiving an item (by walking into it, for example).
     */
    public int animationsToGo;
    private Item field_151002_e;
    /**
     * A NBTTagMap containing data about an ItemStack. Can only be used for non stackable items
     */
    public NBTTagCompound stackTagCompound;
    /** Damage dealt to the item or number of use. Raise when using items. */
    int itemDamage;
    /** Item frame this stack is on, or null if not on an item frame. */
    private EntityItemFrame itemFrame;
    private static final String __OBFID = "CL_00000043";

    public ItemStack(Block par1Block)
    {
        this(par1Block, 1);
    }

    public ItemStack(Block par1Block, int par2)
    {
        this(par1Block, par2, 0);
    }

    public ItemStack(Block par1Block, int par2, int par3)
    {
        this(Item.getItemFromBlock(par1Block), par2, par3);
    }

    public ItemStack(Item par1Item)
    {
        this(par1Item, 1);
    }

    public ItemStack(Item par1Item, int par2)
    {
        this(par1Item, par2, 0);
    }

    public ItemStack(Item par1Item, int par2, int par3)
    {
        this.field_151002_e = par1Item;
        this.stackSize = par2;
        this.itemDamage = par3;

        if (this.itemDamage < 0)
        {
            this.itemDamage = 0;
        }
    }

    public static ItemStack loadItemStackFromNBT(NBTTagCompound par0NBTTagCompound)
    {
        ItemStack itemstack = new ItemStack();
        itemstack.readFromNBT(par0NBTTagCompound);
        return itemstack.getItem() != null ? itemstack : null;
    }

    private ItemStack() {}

    /**
     * Remove the argument from the stack size. Return a new stack object with argument size.
     */
    public ItemStack splitStack(int par1)
    {
        ItemStack itemstack = new ItemStack(this.field_151002_e, par1, this.itemDamage);

        if (this.stackTagCompound != null)
        {
            itemstack.stackTagCompound = (NBTTagCompound)this.stackTagCompound.copy();
        }

        this.stackSize -= par1;
        return itemstack;
    }

    /**
     * Returns the object corresponding to the stack.
     */
    public Item getItem()
    {
        return this.field_151002_e;
    }

    /**
     * Returns the icon index of the current stack.
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex()
    {
        return this.getItem().getIconIndex(this);
    }

    @SideOnly(Side.CLIENT)
    public int getItemSpriteNumber()
    {
        return this.getItem().getSpriteNumber();
    }

    public boolean tryPlaceItemIntoWorld(EntityPlayer par1EntityPlayer, World par2World, int par3, int par4, int par5, int par6, float par7, float par8, float par9)
    {
        boolean flag = this.getItem().onItemUse(this, par1EntityPlayer, par2World, par3, par4, par5, par6, par7, par8, par9);

        if (flag)
        {
            par1EntityPlayer.addStat(StatList.objectUseStats[Item.getIdFromItem(this.field_151002_e)], 1);
        }

        return flag;
    }

    public float func_150997_a(Block p_150997_1_)
    {
        return this.getItem().func_150893_a(this, p_150997_1_);
    }

    /**
     * Called whenever this item stack is equipped and right clicked. Returns the new item stack to put in the position
     * where this item is. Args: world, player
     */
    public ItemStack useItemRightClick(World par1World, EntityPlayer par2EntityPlayer)
    {
        return this.getItem().onItemRightClick(this, par1World, par2EntityPlayer);
    }

    public ItemStack onFoodEaten(World par1World, EntityPlayer par2EntityPlayer)
    {
        return this.getItem().onEaten(this, par1World, par2EntityPlayer);
    }

    /**
     * Write the stack fields to a NBT object. Return the new NBT object.
     */
    public NBTTagCompound writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setShort("id", (short)Item.getIdFromItem(this.field_151002_e));
        par1NBTTagCompound.setByte("Count", (byte)this.stackSize);
        par1NBTTagCompound.setShort("Damage", (short)this.itemDamage);

        if (this.stackTagCompound != null)
        {
            par1NBTTagCompound.setTag("tag", this.stackTagCompound);
        }

        return par1NBTTagCompound;
    }

    /**
     * Read the stack fields from a NBT object.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        this.field_151002_e = Item.getItemById(par1NBTTagCompound.getShort("id"));
        this.stackSize = par1NBTTagCompound.getByte("Count");
        this.itemDamage = par1NBTTagCompound.getShort("Damage");

        if (this.itemDamage < 0)
        {
            this.itemDamage = 0;
        }

        if (par1NBTTagCompound.hasKey("tag", 10))
        {
            this.stackTagCompound = par1NBTTagCompound.getCompoundTag("tag");
        }
    }

    /**
     * Returns maximum size of the stack.
     */
    public int getMaxStackSize()
    {
        return this.getItem().getItemStackLimit(this);
    }

    /**
     * Returns true if the ItemStack can hold 2 or more units of the item.
     */
    public boolean isStackable()
    {
        return this.getMaxStackSize() > 1 && (!this.isItemStackDamageable() || !this.isItemDamaged());
    }

    /**
     * true if this itemStack is damageable
     */
    public boolean isItemStackDamageable()
    {
        return this.field_151002_e.getMaxDamage(this) <= 0 ? false : !this.hasTagCompound() || !this.getTagCompound().getBoolean("Unbreakable");
    }

    public boolean getHasSubtypes()
    {
        return this.field_151002_e.getHasSubtypes();
    }

    /**
     * returns true when a damageable item is damaged
     */
    public boolean isItemDamaged()
    {
        return this.isItemStackDamageable() && getItem().isDamaged(this);
    }

    /**
     * gets the damage of an itemstack, for displaying purposes
     */
    public int getItemDamageForDisplay()
    {
        /**
         * Returns the object corresponding to the stack.
         */
        return getItem().getDisplayDamage(this);
    }

    /**
     * gets the damage of an itemstack
     */
    public int getItemDamage()
    {
        /**
         * Returns the object corresponding to the stack.
         */
        return getItem().getDamage(this);
    }

    /**
     * Sets the item damage of the ItemStack.
     */
    public void setItemDamage(int par1)
    {
        getItem().setDamage(this, par1);
    }

    /**
     * Returns the max damage an item in the stack can take.
     */
    public int getMaxDamage()
    {
        /**
         * Returns the object corresponding to the stack.
         */
        return getItem().getMaxDamage(this);
    }

    /**
     * Attempts to damage the ItemStack with par1 amount of damage, If the ItemStack has the Unbreaking enchantment
     * there is a chance for each point of damage to be negated. Returns true if it takes more damage than
     * getMaxDamage(). Returns false otherwise or if the ItemStack can't be damaged or if all points of damage are
     * negated.
     */
    public boolean attemptDamageItem(int par1, Random par2Random)
    {
        if (!this.isItemStackDamageable())
        {
            return false;
        }
        else
        {
            if (par1 > 0)
            {
                int j = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, this);
                int k = 0;

                for (int l = 0; j > 0 && l < par1; ++l)
                {
                    if (EnchantmentDurability.negateDamage(this, j, par2Random))
                    {
                        ++k;
                    }
                }

                par1 -= k;

                if (par1 <= 0)
                {
                    return false;
                }
            }

            setItemDamage(getItemDamage() + par1); //Redirect through Item's callback if applicable.
            /**
             * gets the damage of an itemstack
             */
            return getItemDamage() > getMaxDamage();
        }
    }

    /**
     * Damages the item in the ItemStack
     */
    public void damageItem(int par1, EntityLivingBase par2EntityLivingBase)
    {
        if (!(par2EntityLivingBase instanceof EntityPlayer) || !((EntityPlayer)par2EntityLivingBase).capabilities.isCreativeMode)
        {
            if (this.isItemStackDamageable())
            {
                if (this.attemptDamageItem(par1, par2EntityLivingBase.getRNG()))
                {
                    par2EntityLivingBase.renderBrokenItemStack(this);
                    --this.stackSize;

                    if (par2EntityLivingBase instanceof EntityPlayer)
                    {
                        EntityPlayer entityplayer = (EntityPlayer)par2EntityLivingBase;
                        entityplayer.addStat(StatList.objectBreakStats[Item.getIdFromItem(this.field_151002_e)], 1);

                        if (this.stackSize == 0 && this.getItem() instanceof ItemBow)
                        {
                            entityplayer.destroyCurrentEquippedItem();
                        }
                    }

                    if (this.stackSize < 0)
                    {
                        this.stackSize = 0;
                    }

                    this.itemDamage = 0;
                }
            }
        }
    }

    /**
     * Calls the corresponding fct in di
     */
    public void hitEntity(EntityLivingBase par1EntityLivingBase, EntityPlayer par2EntityPlayer)
    {
        boolean flag = this.field_151002_e.hitEntity(this, par1EntityLivingBase, par2EntityPlayer);

        if (flag)
        {
            par2EntityPlayer.addStat(StatList.objectUseStats[Item.getIdFromItem(this.field_151002_e)], 1);
        }
    }

    public void func_150999_a(World p_150999_1_, Block p_150999_2_, int p_150999_3_, int p_150999_4_, int p_150999_5_, EntityPlayer p_150999_6_)
    {
        boolean flag = this.field_151002_e.onBlockDestroyed(this, p_150999_1_, p_150999_2_, p_150999_3_, p_150999_4_, p_150999_5_, p_150999_6_);

        if (flag)
        {
            p_150999_6_.addStat(StatList.objectUseStats[Item.getIdFromItem(this.field_151002_e)], 1);
        }
    }

    public boolean func_150998_b(Block p_150998_1_)
    {
        /**
         * Returns the object corresponding to the stack.
         */
        return getItem().canHarvestBlock(p_150998_1_, this);
    }

    public boolean interactWithEntity(EntityPlayer par1EntityPlayer, EntityLivingBase par2EntityLivingBase)
    {
        return this.field_151002_e.itemInteractionForEntity(this, par1EntityPlayer, par2EntityLivingBase);
    }

    /**
     * Returns a new stack with the same properties.
     */
    public ItemStack copy()
    {
        ItemStack itemstack = new ItemStack(this.field_151002_e, this.stackSize, this.itemDamage);

        if (this.stackTagCompound != null)
        {
            itemstack.stackTagCompound = (NBTTagCompound)this.stackTagCompound.copy();
        }

        return itemstack;
    }

    public static boolean areItemStackTagsEqual(ItemStack par0ItemStack, ItemStack par1ItemStack)
    {
        return par0ItemStack == null && par1ItemStack == null ? true : (par0ItemStack != null && par1ItemStack != null ? (par0ItemStack.stackTagCompound == null && par1ItemStack.stackTagCompound != null ? false : par0ItemStack.stackTagCompound == null || par0ItemStack.stackTagCompound.equals(par1ItemStack.stackTagCompound)) : false);
    }

    /**
     * compares ItemStack argument1 with ItemStack argument2; returns true if both ItemStacks are equal
     */
    public static boolean areItemStacksEqual(ItemStack par0ItemStack, ItemStack par1ItemStack)
    {
        return par0ItemStack == null && par1ItemStack == null ? true : (par0ItemStack != null && par1ItemStack != null ? par0ItemStack.isItemStackEqual(par1ItemStack) : false);
    }

    /**
     * compares ItemStack argument to the instance ItemStack; returns true if both ItemStacks are equal
     */
    private boolean isItemStackEqual(ItemStack par1ItemStack)
    {
        return this.stackSize != par1ItemStack.stackSize ? false : (this.field_151002_e != par1ItemStack.field_151002_e ? false : (this.itemDamage != par1ItemStack.itemDamage ? false : (this.stackTagCompound == null && par1ItemStack.stackTagCompound != null ? false : this.stackTagCompound == null || this.stackTagCompound.equals(par1ItemStack.stackTagCompound))));
    }

    /**
     * compares ItemStack argument to the instance ItemStack; returns true if the Items contained in both ItemStacks are
     * equal
     */
    public boolean isItemEqual(ItemStack par1ItemStack)
    {
        return this.field_151002_e == par1ItemStack.field_151002_e && this.itemDamage == par1ItemStack.itemDamage;
    }

    public String getUnlocalizedName()
    {
        return this.field_151002_e.getUnlocalizedName(this);
    }

    /**
     * Creates a copy of a ItemStack, a null parameters will return a null.
     */
    public static ItemStack copyItemStack(ItemStack par0ItemStack)
    {
        return par0ItemStack == null ? null : par0ItemStack.copy();
    }

    public String toString()
    {
        return this.stackSize + "x" + this.field_151002_e.getUnlocalizedName() + "@" + this.itemDamage;
    }

    /**
     * Called each tick as long the ItemStack in on player inventory. Used to progress the pickup animation and update
     * maps.
     */
    public void updateAnimation(World par1World, Entity par2Entity, int par3, boolean par4)
    {
        if (this.animationsToGo > 0)
        {
            --this.animationsToGo;
        }

        this.field_151002_e.onUpdate(this, par1World, par2Entity, par3, par4);
    }

    public void onCrafting(World par1World, EntityPlayer par2EntityPlayer, int par3)
    {
        par2EntityPlayer.addStat(StatList.objectCraftStats[Item.getIdFromItem(this.field_151002_e)], par3);
        this.field_151002_e.onCreated(this, par1World, par2EntityPlayer);
    }

    public int getMaxItemUseDuration()
    {
        return this.getItem().getMaxItemUseDuration(this);
    }

    public EnumAction getItemUseAction()
    {
        return this.getItem().getItemUseAction(this);
    }

    /**
     * Called when the player releases the use item button. Args: world, entityplayer, itemInUseCount
     */
    public void onPlayerStoppedUsing(World par1World, EntityPlayer par2EntityPlayer, int par3)
    {
        this.getItem().onPlayerStoppedUsing(this, par1World, par2EntityPlayer, par3);
    }

    /**
     * Returns true if the ItemStack has an NBTTagCompound. Currently used to store enchantments.
     */
    public boolean hasTagCompound()
    {
        return this.stackTagCompound != null;
    }

    /**
     * Returns the NBTTagCompound of the ItemStack.
     */
    public NBTTagCompound getTagCompound()
    {
        return this.stackTagCompound;
    }

    public NBTTagList getEnchantmentTagList()
    {
        return this.stackTagCompound == null ? null : this.stackTagCompound.getTagList("ench", 10);
    }

    /**
     * Assigns a NBTTagCompound to the ItemStack, minecraft validates that only non-stackable items can have it.
     */
    public void setTagCompound(NBTTagCompound par1NBTTagCompound)
    {
        this.stackTagCompound = par1NBTTagCompound;
    }

    /**
     * returns the display name of the itemstack
     */
    public String getDisplayName()
    {
        String s = this.getItem().getItemStackDisplayName(this);

        if (this.stackTagCompound != null && this.stackTagCompound.hasKey("display", 10))
        {
            NBTTagCompound nbttagcompound = this.stackTagCompound.getCompoundTag("display");

            if (nbttagcompound.hasKey("Name", 8))
            {
                s = nbttagcompound.getString("Name");
            }
        }

        return s;
    }

    public ItemStack setStackDisplayName(String p_151001_1_)
    {
        if (this.stackTagCompound == null)
        {
            this.stackTagCompound = new NBTTagCompound();
        }

        if (!this.stackTagCompound.hasKey("display", 10))
        {
            this.stackTagCompound.setTag("display", new NBTTagCompound());
        }

        this.stackTagCompound.getCompoundTag("display").setString("Name", p_151001_1_);
        return this;
    }

    public void func_135074_t()
    {
        if (this.stackTagCompound != null)
        {
            if (this.stackTagCompound.hasKey("display", 10))
            {
                NBTTagCompound nbttagcompound = this.stackTagCompound.getCompoundTag("display");
                nbttagcompound.removeTag("Name");

                if (nbttagcompound.hasNoTags())
                {
                    this.stackTagCompound.removeTag("display");

                    if (this.stackTagCompound.hasNoTags())
                    {
                        this.setTagCompound((NBTTagCompound)null);
                    }
                }
            }
        }
    }

    /**
     * Returns true if the itemstack has a display name
     */
    public boolean hasDisplayName()
    {
        return this.stackTagCompound == null ? false : (!this.stackTagCompound.hasKey("display", 10) ? false : this.stackTagCompound.getCompoundTag("display").hasKey("Name", 8));
    }

    /**
     * Return a list of strings containing information about the item
     */
    @SideOnly(Side.CLIENT)
    public List getTooltip(EntityPlayer par1EntityPlayer, boolean par2)
    {
        ArrayList arraylist = new ArrayList();
        String s = this.getDisplayName();

        if (this.hasDisplayName())
        {
            s = EnumChatFormatting.ITALIC + s + EnumChatFormatting.RESET;
        }

        int i;

        if (par2)
        {
            String s1 = "";

            if (s.length() > 0)
            {
                s = s + " (";
                s1 = ")";
            }

            i = Item.getIdFromItem(this.field_151002_e);

            if (this.getHasSubtypes())
            {
                s = s + String.format("#%04d/%d%s", new Object[] {Integer.valueOf(i), Integer.valueOf(this.itemDamage), s1});
            }
            else
            {
                s = s + String.format("#%04d%s", new Object[] {Integer.valueOf(i), s1});
            }
        }
        else if (!this.hasDisplayName() && this.field_151002_e == Items.filled_map)
        {
            s = s + " #" + this.itemDamage;
        }

        arraylist.add(s);
        this.field_151002_e.addInformation(this, par1EntityPlayer, arraylist, par2);

        if (this.hasTagCompound())
        {
            NBTTagList nbttaglist = this.getEnchantmentTagList();

            if (nbttaglist != null)
            {
                for (i = 0; i < nbttaglist.tagCount(); ++i)
                {
                    short short1 = nbttaglist.getCompoundTagAt(i).getShort("id");
                    short short2 = nbttaglist.getCompoundTagAt(i).getShort("lvl");

                    if (Enchantment.enchantmentsList[short1] != null)
                    {
                        arraylist.add(Enchantment.enchantmentsList[short1].getTranslatedName(short2));
                    }
                }
            }

            if (this.stackTagCompound.hasKey("display", 10))
            {
                NBTTagCompound nbttagcompound = this.stackTagCompound.getCompoundTag("display");

                if (nbttagcompound.hasKey("color", 3))
                {
                    if (par2)
                    {
                        arraylist.add("Color: #" + Integer.toHexString(nbttagcompound.getInteger("color")).toUpperCase());
                    }
                    else
                    {
                        arraylist.add(EnumChatFormatting.ITALIC + StatCollector.translateToLocal("item.dyed"));
                    }
                }

                if (nbttagcompound.func_150299_b("Lore") == 9)
                {
                    NBTTagList nbttaglist1 = nbttagcompound.getTagList("Lore", 8);

                    if (nbttaglist1.tagCount() > 0)
                    {
                        for (int j = 0; j < nbttaglist1.tagCount(); ++j)
                        {
                            arraylist.add(EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.ITALIC + nbttaglist1.getStringTagAt(j));
                        }
                    }
                }
            }
        }

        Multimap multimap = this.getAttributeModifiers();

        if (!multimap.isEmpty())
        {
            arraylist.add("");
            Iterator iterator = multimap.entries().iterator();

            while (iterator.hasNext())
            {
                Entry entry = (Entry)iterator.next();
                AttributeModifier attributemodifier = (AttributeModifier)entry.getValue();
                double d0 = attributemodifier.getAmount();
                double d1;

                if (attributemodifier.getOperation() != 1 && attributemodifier.getOperation() != 2)
                {
                    d1 = attributemodifier.getAmount();
                }
                else
                {
                    d1 = attributemodifier.getAmount() * 100.0D;
                }

                if (d0 > 0.0D)
                {
                    arraylist.add(EnumChatFormatting.BLUE + StatCollector.translateToLocalFormatted("attribute.modifier.plus." + attributemodifier.getOperation(), new Object[] {field_111284_a.format(d1), StatCollector.translateToLocal("attribute.name." + (String)entry.getKey())}));
                }
                else if (d0 < 0.0D)
                {
                    d1 *= -1.0D;
                    arraylist.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("attribute.modifier.take." + attributemodifier.getOperation(), new Object[] {field_111284_a.format(d1), StatCollector.translateToLocal("attribute.name." + (String)entry.getKey())}));
                }
            }
        }

        if (this.hasTagCompound() && this.getTagCompound().getBoolean("Unbreakable"))
        {
            arraylist.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("item.unbreakable"));
        }

        if (par2 && this.isItemDamaged())
        {
            arraylist.add("Durability: " + (this.getMaxDamage() - this.getItemDamageForDisplay()) + " / " + this.getMaxDamage());
        }
        ForgeEventFactory.onItemTooltip(this, par1EntityPlayer, arraylist, par2);

        return arraylist;
    }

    @Deprecated
    @SideOnly(Side.CLIENT)
    public boolean hasEffect()
    {
        return hasEffect(0);
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(int pass)
    {
        return this.getItem().hasEffect(this, pass);
    }

    public EnumRarity getRarity()
    {
        return this.getItem().getRarity(this);
    }

    /**
     * True if it is a tool and has no enchantments to begin with
     */
    public boolean isItemEnchantable()
    {
        return !this.getItem().isItemTool(this) ? false : !this.isItemEnchanted();
    }

    /**
     * Adds an enchantment with a desired level on the ItemStack.
     */
    public void addEnchantment(Enchantment par1Enchantment, int par2)
    {
        if (this.stackTagCompound == null)
        {
            this.setTagCompound(new NBTTagCompound());
        }

        if (!this.stackTagCompound.hasKey("ench", 9))
        {
            this.stackTagCompound.setTag("ench", new NBTTagList());
        }

        NBTTagList nbttaglist = this.stackTagCompound.getTagList("ench", 10);
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setShort("id", (short)par1Enchantment.effectId);
        nbttagcompound.setShort("lvl", (short)((byte)par2));
        nbttaglist.appendTag(nbttagcompound);
    }

    /**
     * True if the item has enchantment data
     */
    public boolean isItemEnchanted()
    {
        return this.stackTagCompound != null && this.stackTagCompound.hasKey("ench", 9);
    }

    public void setTagInfo(String par1Str, NBTBase par2NBTBase)
    {
        if (this.stackTagCompound == null)
        {
            this.setTagCompound(new NBTTagCompound());
        }

        this.stackTagCompound.setTag(par1Str, par2NBTBase);
    }

    public boolean canEditBlocks()
    {
        return this.getItem().canItemEditBlocks();
    }

    /**
     * Return whether this stack is on an item frame.
     */
    public boolean isOnItemFrame()
    {
        return this.itemFrame != null;
    }

    /**
     * Set the item frame this stack is on.
     */
    public void setItemFrame(EntityItemFrame par1EntityItemFrame)
    {
        this.itemFrame = par1EntityItemFrame;
    }

    /**
     * Return the item frame this stack is on. Returns null if not on an item frame.
     */
    public EntityItemFrame getItemFrame()
    {
        return this.itemFrame;
    }

    /**
     * Get this stack's repair cost, or 0 if no repair cost is defined.
     */
    public int getRepairCost()
    {
        return this.hasTagCompound() && this.stackTagCompound.hasKey("RepairCost", 3) ? this.stackTagCompound.getInteger("RepairCost") : 0;
    }

    /**
     * Set this stack's repair cost.
     */
    public void setRepairCost(int par1)
    {
        if (!this.hasTagCompound())
        {
            this.stackTagCompound = new NBTTagCompound();
        }

        this.stackTagCompound.setInteger("RepairCost", par1);
    }

    /**
     * Gets the attribute modifiers for this ItemStack.nWill check for an NBT tag list containing modifiers for the
     * stack.
     */
    public Multimap getAttributeModifiers()
    {
        Object object;

        if (this.hasTagCompound() && this.stackTagCompound.hasKey("AttributeModifiers", 9))
        {
            object = HashMultimap.create();
            NBTTagList nbttaglist = this.stackTagCompound.getTagList("AttributeModifiers", 10);

            for (int i = 0; i < nbttaglist.tagCount(); ++i)
            {
                NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
                AttributeModifier attributemodifier = SharedMonsterAttributes.readAttributeModifierFromNBT(nbttagcompound);

                if (attributemodifier.getID().getLeastSignificantBits() != 0L && attributemodifier.getID().getMostSignificantBits() != 0L)
                {
                    ((Multimap)object).put(nbttagcompound.getString("AttributeName"), attributemodifier);
                }
            }
        }
        else
        {
            object = this.getItem().getAttributeModifiers(this);
        }

        return (Multimap)object;
    }

    public void func_150996_a(Item p_150996_1_)
    {
        this.field_151002_e = p_150996_1_;
    }

    public IChatComponent func_151000_E()
    {
        IChatComponent ichatcomponent = (new ChatComponentText("[")).appendText(this.getDisplayName()).appendText("]");

        if (this.field_151002_e != null)
        {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            this.writeToNBT(nbttagcompound);
            ichatcomponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ChatComponentText(nbttagcompound.toString())));
            ichatcomponent.getChatStyle().setColor(this.getRarity().rarityColor);
        }

        return ichatcomponent;
    }
}