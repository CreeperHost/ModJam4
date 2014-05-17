package net.creeperhost.harken.item;

import net.creeperhost.harken.reference.ItemInfo;
import net.creeperhost.harken.reference.ModInfo;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemGlassesSide extends Item {
	public ItemGlassesSide() {
		super();
		setUnlocalizedName(ItemInfo.GLASSES_SIDE_NAME);
		setTextureName(ModInfo.ID + ":" + ItemInfo.GLASSES_SIDE_TEXTURE);
		setCreativeTab(CreativeTabs.tabRedstone);
	}
}
