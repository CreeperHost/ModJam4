package net.creeperhost.modjam4.item;

import net.creeperhost.modjam4.reference.ItemInfo;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemGlassesNoseBridge extends Item {
	public ItemGlassesNoseBridge() {
		super();
		setUnlocalizedName(ItemInfo.GLASSES_NOSE_BRIDGE_NAME);
		setTextureName(ItemInfo.GLASSES_NOSE_BRIDGE_TEXTURE);
		setCreativeTab(CreativeTabs.tabRedstone);
	}
}
