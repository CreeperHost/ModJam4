package net.minecraft.village;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class MerchantRecipe
{
    /** Item the Villager buys. */
    private ItemStack itemToBuy;
    /** Second Item the Villager buys. */
    private ItemStack secondItemToBuy;
    /** Item the Villager sells. */
    private ItemStack itemToSell;
    /**
     * Saves how much has been tool used when put into to slot to be enchanted.
     */
    private int toolUses;
    /** Maximum times this trade can be used. */
    private int maxTradeUses;
    private static final String __OBFID = "CL_00000126";

    public MerchantRecipe(NBTTagCompound par1NBTTagCompound)
    {
        this.readFromTags(par1NBTTagCompound);
    }

    public MerchantRecipe(ItemStack par1ItemStack, ItemStack par2ItemStack, ItemStack par3ItemStack)
    {
        this.itemToBuy = par1ItemStack;
        this.secondItemToBuy = par2ItemStack;
        this.itemToSell = par3ItemStack;
        this.maxTradeUses = 7;
    }

    public MerchantRecipe(ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
        this(par1ItemStack, (ItemStack)null, par2ItemStack);
    }

    public MerchantRecipe(ItemStack par1ItemStack, Item par2Item)
    {
        this(par1ItemStack, new ItemStack(par2Item));
    }

    /**
     * Gets the itemToBuy.
     */
    public ItemStack getItemToBuy()
    {
        return this.itemToBuy;
    }

    /**
     * Gets secondItemToBuy.
     */
    public ItemStack getSecondItemToBuy()
    {
        return this.secondItemToBuy;
    }

    /**
     * Gets if Villager has secondItemToBuy.
     */
    public boolean hasSecondItemToBuy()
    {
        return this.secondItemToBuy != null;
    }

    /**
     * Gets itemToSell.
     */
    public ItemStack getItemToSell()
    {
        return this.itemToSell;
    }

    /**
     * checks if both the first and second ItemToBuy IDs are the same
     */
    public boolean hasSameIDsAs(MerchantRecipe par1MerchantRecipe)
    {
        return this.itemToBuy.getItem() == par1MerchantRecipe.itemToBuy.getItem() && this.itemToSell.getItem() == par1MerchantRecipe.itemToSell.getItem() ? this.secondItemToBuy == null && par1MerchantRecipe.secondItemToBuy == null || this.secondItemToBuy != null && par1MerchantRecipe.secondItemToBuy != null && this.secondItemToBuy.getItem() == par1MerchantRecipe.secondItemToBuy.getItem() : false;
    }

    /**
     * checks first and second ItemToBuy ID's and count. Calls hasSameIDs
     */
    public boolean hasSameItemsAs(MerchantRecipe par1MerchantRecipe)
    {
        return this.hasSameIDsAs(par1MerchantRecipe) && (this.itemToBuy.stackSize < par1MerchantRecipe.itemToBuy.stackSize || this.secondItemToBuy != null && this.secondItemToBuy.stackSize < par1MerchantRecipe.secondItemToBuy.stackSize);
    }

    public void incrementToolUses()
    {
        ++this.toolUses;
    }

    public void func_82783_a(int par1)
    {
        this.maxTradeUses += par1;
    }

    public boolean isRecipeDisabled()
    {
        return this.toolUses >= this.maxTradeUses;
    }

    @SideOnly(Side.CLIENT)
    public void func_82785_h()
    {
        this.toolUses = this.maxTradeUses;
    }

    public void readFromTags(NBTTagCompound par1NBTTagCompound)
    {
        NBTTagCompound nbttagcompound1 = par1NBTTagCompound.getCompoundTag("buy");
        this.itemToBuy = ItemStack.loadItemStackFromNBT(nbttagcompound1);
        NBTTagCompound nbttagcompound2 = par1NBTTagCompound.getCompoundTag("sell");
        this.itemToSell = ItemStack.loadItemStackFromNBT(nbttagcompound2);

        if (par1NBTTagCompound.hasKey("buyB", 10))
        {
            this.secondItemToBuy = ItemStack.loadItemStackFromNBT(par1NBTTagCompound.getCompoundTag("buyB"));
        }

        if (par1NBTTagCompound.hasKey("uses", 99))
        {
            this.toolUses = par1NBTTagCompound.getInteger("uses");
        }

        if (par1NBTTagCompound.hasKey("maxUses", 99))
        {
            this.maxTradeUses = par1NBTTagCompound.getInteger("maxUses");
        }
        else
        {
            this.maxTradeUses = 7;
        }
    }

    public NBTTagCompound writeToTags()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setTag("buy", this.itemToBuy.writeToNBT(new NBTTagCompound()));
        nbttagcompound.setTag("sell", this.itemToSell.writeToNBT(new NBTTagCompound()));

        if (this.secondItemToBuy != null)
        {
            nbttagcompound.setTag("buyB", this.secondItemToBuy.writeToNBT(new NBTTagCompound()));
        }

        nbttagcompound.setInteger("uses", this.toolUses);
        nbttagcompound.setInteger("maxUses", this.maxTradeUses);
        return nbttagcompound;
    }
}