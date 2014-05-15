package net.creeperhost.modjam4.item;

import net.creeperhost.modjam4.reference.ItemInfo;
import net.creeperhost.modjam4.reference.ModInfo;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ItemGlasses extends ItemArmor {
	public ItemGlasses(ArmorMaterial armorMaterial, int type) {
		super(armorMaterial, 0, type);
		setUnlocalizedName(ItemInfo.GLASSES_NAME);
		setTextureName(ModInfo.ID + ":" + ItemInfo.GLASSES_TEXTURE);
		setCreativeTab(CreativeTabs.tabRedstone);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		if (stack.getItem() == ModItems.glasses) {
			return ModInfo.ID + ":" + ItemInfo.GLASSES_MODEL;
		} else {
			return null;
		}
	}
}
