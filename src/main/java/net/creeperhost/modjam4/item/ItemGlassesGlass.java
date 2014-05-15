package net.creeperhost.modjam4.item;

import net.creeperhost.modjam4.reference.ItemInfo;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemGlassesGlass extends Item {
	public ItemGlassesGlass() {
		super();
		setUnlocalizedName(ItemInfo.GLASSES_SIDE_NAME);
		setTextureName(ItemInfo.GLASSES_SIDE_TEXTURE);
		setCreativeTab(CreativeTabs.tabRedstone);
	}
}
