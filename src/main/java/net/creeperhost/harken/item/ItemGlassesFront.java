package net.creeperhost.harken.item;

import net.creeperhost.harken.reference.ItemInfo;
import net.creeperhost.harken.reference.ModInfo;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemGlassesFront extends Item {
	public ItemGlassesFront() {
		super();
		setUnlocalizedName(ItemInfo.GLASSES_FRONT_NAME);
		setTextureName(ModInfo.ID + ":" + ItemInfo.GLASSES_FRONT_TEXTURE);
		setCreativeTab(CreativeTabs.tabRedstone);
	}
}
