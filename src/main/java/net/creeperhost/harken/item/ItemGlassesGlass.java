package net.creeperhost.harken.item;

import net.creeperhost.harken.reference.ItemInfo;
import net.creeperhost.harken.reference.ModInfo;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemGlassesGlass extends Item {
	public ItemGlassesGlass() {
		super();
		setUnlocalizedName(ItemInfo.GLASSES_GLASS_NAME);
		setTextureName(ModInfo.ID + ":" + ItemInfo.GLASSES_GLASS_TEXTURE);
		setCreativeTab(CreativeTabs.tabRedstone);
	}
}
