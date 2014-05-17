package net.creeperhost.harken.item;

import net.creeperhost.harken.reference.ItemInfo;
import net.creeperhost.harken.reference.ModInfo;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemGlassesNoseBridge extends Item {
	public ItemGlassesNoseBridge() {
		super();
		setUnlocalizedName(ItemInfo.GLASSES_NOSE_BRIDGE_NAME);
		setTextureName(ModInfo.ID + ":" + ItemInfo.GLASSES_NOSE_BRIDGE_TEXTURE);
		setCreativeTab(CreativeTabs.tabRedstone);
	}
}
