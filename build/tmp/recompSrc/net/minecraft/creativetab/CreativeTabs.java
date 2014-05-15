package net.minecraft.creativetab;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Iterator;
import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class CreativeTabs
{
    public static CreativeTabs[] creativeTabArray = new CreativeTabs[12];
    public static final CreativeTabs tabBlock = new CreativeTabs(0, "buildingBlocks")
    {
        private static final String __OBFID = "CL_00000010";
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem()
        {
            return Item.getItemFromBlock(Blocks.brick_block);
        }
    };
    public static final CreativeTabs tabDecorations = new CreativeTabs(1, "decorations")
    {
        private static final String __OBFID = "CL_00000011";
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem()
        {
            return Item.getItemFromBlock(Blocks.double_plant);
        }
        @SideOnly(Side.CLIENT)
        public int func_151243_f()
        {
            return 5;
        }
    };
    public static final CreativeTabs tabRedstone = new CreativeTabs(2, "redstone")
    {
        private static final String __OBFID = "CL_00000012";
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem()
        {
            return Items.redstone;
        }
    };
    public static final CreativeTabs tabTransport = new CreativeTabs(3, "transportation")
    {
        private static final String __OBFID = "CL_00000014";
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem()
        {
            return Item.getItemFromBlock(Blocks.golden_rail);
        }
    };
    public static final CreativeTabs tabMisc = (new CreativeTabs(4, "misc")
    {
        private static final String __OBFID = "CL_00000015";
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem()
        {
            return Items.lava_bucket;
        }
    }).func_111229_a(new EnumEnchantmentType[] {EnumEnchantmentType.all});
    public static final CreativeTabs tabAllSearch = (new CreativeTabs(5, "search")
    {
        private static final String __OBFID = "CL_00000016";
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem()
        {
            return Items.compass;
        }
    }).setBackgroundImageName("item_search.png");
    public static final CreativeTabs tabFood = new CreativeTabs(6, "food")
    {
        private static final String __OBFID = "CL_00000017";
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem()
        {
            return Items.apple;
        }
    };
    public static final CreativeTabs tabTools = (new CreativeTabs(7, "tools")
    {
        private static final String __OBFID = "CL_00000018";
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem()
        {
            return Items.iron_axe;
        }
    }).func_111229_a(new EnumEnchantmentType[] {EnumEnchantmentType.digger, EnumEnchantmentType.fishing_rod, EnumEnchantmentType.breakable});
    public static final CreativeTabs tabCombat = (new CreativeTabs(8, "combat")
    {
        private static final String __OBFID = "CL_00000007";
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem()
        {
            return Items.golden_sword;
        }
    }).func_111229_a(new EnumEnchantmentType[] {EnumEnchantmentType.armor, EnumEnchantmentType.armor_feet, EnumEnchantmentType.armor_head, EnumEnchantmentType.armor_legs, EnumEnchantmentType.armor_torso, EnumEnchantmentType.bow, EnumEnchantmentType.weapon});
    public static final CreativeTabs tabBrewing = new CreativeTabs(9, "brewing")
    {
        private static final String __OBFID = "CL_00000008";
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem()
        {
            return Items.potionitem;
        }
    };
    public static final CreativeTabs tabMaterials = new CreativeTabs(10, "materials")
    {
        private static final String __OBFID = "CL_00000009";
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem()
        {
            return Items.stick;
        }
    };
    public static final CreativeTabs tabInventory = (new CreativeTabs(11, "inventory")
    {
        private static final String __OBFID = "CL_00000006";
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem()
        {
            return Item.getItemFromBlock(Blocks.chest);
        }
    }).setBackgroundImageName("inventory.png").setNoScrollbar().setNoTitle();
    private final int tabIndex;
    private final String tabLabel;
    /** Texture to use. */
    private String backgroundImageName = "items.png";
    private boolean hasScrollbar = true;
    /** Whether to draw the title in the foreground of the creative GUI */
    private boolean drawTitle = true;
    private EnumEnchantmentType[] field_111230_s;
    @SideOnly(Side.CLIENT)
    private ItemStack field_151245_t;
    private static final String __OBFID = "CL_00000005";

    public CreativeTabs(String lable)
    {
        this(getNextID(), lable);
    }

    public CreativeTabs(int par1, String par2Str)
    {
        if (par1 >= creativeTabArray.length)
        {
            CreativeTabs[] tmp = new CreativeTabs[par1 + 1];
            for (int x = 0; x < creativeTabArray.length; x++)
            {
                tmp[x] = creativeTabArray[x];
            }
            creativeTabArray = tmp;
        }
        this.tabIndex = par1;
        this.tabLabel = par2Str;
        creativeTabArray[par1] = this;
    }

    @SideOnly(Side.CLIENT)
    public int getTabIndex()
    {
        return this.tabIndex;
    }

    public CreativeTabs setBackgroundImageName(String par1Str)
    {
        this.backgroundImageName = par1Str;
        return this;
    }

    @SideOnly(Side.CLIENT)
    public String getTabLabel()
    {
        return this.tabLabel;
    }

    /**
     * Gets the translated Label.
     */
    @SideOnly(Side.CLIENT)
    public String getTranslatedTabLabel()
    {
        return "itemGroup." + this.getTabLabel();
    }

    @SideOnly(Side.CLIENT)
    public ItemStack getIconItemStack()
    {
        if (this.field_151245_t == null)
        {
            this.field_151245_t = new ItemStack(this.getTabIconItem(), 1, this.func_151243_f());
        }

        return this.field_151245_t;
    }

    @SideOnly(Side.CLIENT)
    public abstract Item getTabIconItem();

    @SideOnly(Side.CLIENT)
    public int func_151243_f()
    {
        return 0;
    }

    @SideOnly(Side.CLIENT)
    public String getBackgroundImageName()
    {
        return this.backgroundImageName;
    }

    @SideOnly(Side.CLIENT)
    public boolean drawInForegroundOfTab()
    {
        return this.drawTitle;
    }

    public CreativeTabs setNoTitle()
    {
        this.drawTitle = false;
        return this;
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldHidePlayerInventory()
    {
        return this.hasScrollbar;
    }

    public CreativeTabs setNoScrollbar()
    {
        this.hasScrollbar = false;
        return this;
    }

    /**
     * returns index % 6
     */
    @SideOnly(Side.CLIENT)
    public int getTabColumn()
    {
        if (tabIndex > 11)
        {
            return ((tabIndex - 12) % 10) % 5;
        }
        return this.tabIndex % 6;
    }

    /**
     * returns tabIndex < 6
     */
    @SideOnly(Side.CLIENT)
    public boolean isTabInFirstRow()
    {
        if (tabIndex > 11)
        {
            return ((tabIndex - 12) % 10) < 5;
        }
        return this.tabIndex < 6;
    }

    @SideOnly(Side.CLIENT)
    public EnumEnchantmentType[] func_111225_m()
    {
        return this.field_111230_s;
    }

    public CreativeTabs func_111229_a(EnumEnchantmentType ... par1ArrayOfEnumEnchantmentType)
    {
        this.field_111230_s = par1ArrayOfEnumEnchantmentType;
        return this;
    }

    @SideOnly(Side.CLIENT)
    public boolean func_111226_a(EnumEnchantmentType par1EnumEnchantmentType)
    {
        if (this.field_111230_s == null)
        {
            return false;
        }
        else
        {
            EnumEnchantmentType[] aenumenchantmenttype = this.field_111230_s;
            int i = aenumenchantmenttype.length;

            for (int j = 0; j < i; ++j)
            {
                EnumEnchantmentType enumenchantmenttype1 = aenumenchantmenttype[j];

                if (enumenchantmenttype1 == par1EnumEnchantmentType)
                {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * only shows items which have tabToDisplayOn == this
     */
    @SideOnly(Side.CLIENT)
    public void displayAllReleventItems(List par1List)
    {
        Iterator iterator = Item.itemRegistry.iterator();

        while (iterator.hasNext())
        {
            Item item = (Item)iterator.next();

            if (item == null)
            {
                continue;
            }

            for (CreativeTabs tab : item.getCreativeTabs())
            {
                if (tab == this)
                {
                    item.getSubItems(item, this, par1List);
                }
            }
        }

        if (this.func_111225_m() != null)
        {
            this.addEnchantmentBooksToList(par1List, this.func_111225_m());
        }
    }

    /**
     * Adds the enchantment books from the supplied EnumEnchantmentType to the given list.
     */
    @SideOnly(Side.CLIENT)
    public void addEnchantmentBooksToList(List par1List, EnumEnchantmentType ... par2ArrayOfEnumEnchantmentType)
    {
        Enchantment[] aenchantment = Enchantment.enchantmentsList;
        int i = aenchantment.length;

        for (int j = 0; j < i; ++j)
        {
            Enchantment enchantment = aenchantment[j];

            if (enchantment != null && enchantment.type != null)
            {
                boolean flag = false;

                for (int k = 0; k < par2ArrayOfEnumEnchantmentType.length && !flag; ++k)
                {
                    if (enchantment.type == par2ArrayOfEnumEnchantmentType[k])
                    {
                        flag = true;
                    }
                }

                if (flag)
                {
                    par1List.add(Items.enchanted_book.getEnchantedItemStack(new EnchantmentData(enchantment, enchantment.getMaxLevel())));
                }
            }
        }
    }

    public int getTabPage()
    {
        if (tabIndex > 11)
        {
            return ((tabIndex - 12) / 10) + 1;
        }
        return 0;
    }

    public static int getNextID()
    {
        return creativeTabArray.length;
    }

    /**
     * Determines if the search bar should be shown for this tab.
     * 
     * @return True to show the bar
     */
    public boolean hasSearchBar()
    {
        return tabIndex == CreativeTabs.tabAllSearch.tabIndex;
    }
}