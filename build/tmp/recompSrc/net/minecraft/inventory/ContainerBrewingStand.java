package net.minecraft.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntityBrewingStand;

public class ContainerBrewingStand extends Container
{
    private TileEntityBrewingStand tileBrewingStand;
    /** Instance of Slot. */
    private final Slot theSlot;
    private int brewTime;
    private static final String __OBFID = "CL_00001737";

    public ContainerBrewingStand(InventoryPlayer par1InventoryPlayer, TileEntityBrewingStand par2TileEntityBrewingStand)
    {
        this.tileBrewingStand = par2TileEntityBrewingStand;
        this.addSlotToContainer(new ContainerBrewingStand.Potion(par1InventoryPlayer.player, par2TileEntityBrewingStand, 0, 56, 46));
        this.addSlotToContainer(new ContainerBrewingStand.Potion(par1InventoryPlayer.player, par2TileEntityBrewingStand, 1, 79, 53));
        this.addSlotToContainer(new ContainerBrewingStand.Potion(par1InventoryPlayer.player, par2TileEntityBrewingStand, 2, 102, 46));
        this.theSlot = this.addSlotToContainer(new ContainerBrewingStand.Ingredient(par2TileEntityBrewingStand, 3, 79, 17));
        int i;

        for (i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(par1InventoryPlayer, i, 8 + i * 18, 142));
        }
    }

    public void addCraftingToCrafters(ICrafting par1ICrafting)
    {
        super.addCraftingToCrafters(par1ICrafting);
        par1ICrafting.sendProgressBarUpdate(this, 0, this.tileBrewingStand.getBrewTime());
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int i = 0; i < this.crafters.size(); ++i)
        {
            ICrafting icrafting = (ICrafting)this.crafters.get(i);

            if (this.brewTime != this.tileBrewingStand.getBrewTime())
            {
                icrafting.sendProgressBarUpdate(this, 0, this.tileBrewingStand.getBrewTime());
            }
        }

        this.brewTime = this.tileBrewingStand.getBrewTime();
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2)
    {
        if (par1 == 0)
        {
            this.tileBrewingStand.func_145938_d(par2);
        }
    }

    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return this.tileBrewingStand.isUseableByPlayer(par1EntityPlayer);
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if ((par2 < 0 || par2 > 2) && par2 != 3)
            {
                if (!this.theSlot.getHasStack() && this.theSlot.isItemValid(itemstack1))
                {
                    if (!this.mergeItemStack(itemstack1, 3, 4, false))
                    {
                        return null;
                    }
                }
                else if (ContainerBrewingStand.Potion.canHoldPotion(itemstack))
                {
                    if (!this.mergeItemStack(itemstack1, 0, 3, false))
                    {
                        return null;
                    }
                }
                else if (par2 >= 4 && par2 < 31)
                {
                    if (!this.mergeItemStack(itemstack1, 31, 40, false))
                    {
                        return null;
                    }
                }
                else if (par2 >= 31 && par2 < 40)
                {
                    if (!this.mergeItemStack(itemstack1, 4, 31, false))
                    {
                        return null;
                    }
                }
                else if (!this.mergeItemStack(itemstack1, 4, 40, false))
                {
                    return null;
                }
            }
            else
            {
                if (!this.mergeItemStack(itemstack1, 4, 40, true))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
        }

        return itemstack;
    }

    class Ingredient extends Slot
    {
        private static final String __OBFID = "CL_00001738";

        public Ingredient(IInventory par2IInventory, int par3, int par4, int par5)
        {
            super(par2IInventory, par3, par4, par5);
        }

        /**
         * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
         */
        public boolean isItemValid(ItemStack par1ItemStack)
        {
            return par1ItemStack != null ? par1ItemStack.getItem().isPotionIngredient(par1ItemStack) : false;
        }

        /**
         * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the
         * case of armor slots)
         */
        public int getSlotStackLimit()
        {
            return 64;
        }
    }

    static class Potion extends Slot
        {
            /** The player that has this container open. */
            private EntityPlayer player;
            private static final String __OBFID = "CL_00001740";

            public Potion(EntityPlayer par1EntityPlayer, IInventory par2IInventory, int par3, int par4, int par5)
            {
                super(par2IInventory, par3, par4, par5);
                this.player = par1EntityPlayer;
            }

            /**
             * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
             */
            public boolean isItemValid(ItemStack par1ItemStack)
            {
                /**
                 * Returns true if this itemstack can be filled with a potion
                 */
                return canHoldPotion(par1ItemStack);
            }

            /**
             * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in
             * the case of armor slots)
             */
            public int getSlotStackLimit()
            {
                return 1;
            }

            public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack)
            {
                if (par2ItemStack.getItem() instanceof ItemPotion && par2ItemStack.getItemDamage() > 0)
                {
                    this.player.addStat(AchievementList.potion, 1);
                }

                super.onPickupFromSlot(par1EntityPlayer, par2ItemStack);
            }

            /**
             * Returns true if this itemstack can be filled with a potion
             */
            public static boolean canHoldPotion(ItemStack par0ItemStack)
            {
                return par0ItemStack != null && (par0ItemStack.getItem() instanceof ItemPotion || par0ItemStack.getItem() == Items.glass_bottle);
            }
        }
}