package net.creeperhost.modjam4.item;

import net.creeperhost.modjam4.reference.ItemInfo;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemGlassesGlass extends Item {
	public ItemGlassesGlass() {
		super();
		setUnlocalizedName(ItemInfo.GLASSES_GLASS_NAME);
		setTextureName(ItemInfo.GLASSES_GLASS_TEXTURE);
		setCreativeTab(CreativeTabs.tabRedstone);
	}
}
