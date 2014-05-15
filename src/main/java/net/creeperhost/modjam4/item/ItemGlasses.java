package net.creeperhost.modjam4.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ItemGlasses extends ItemArmor {
	public ItemGlasses(ArmorMaterial armorMaterial, int type) {
		super(armorMaterial, 0, type);
		setUnlocalizedName("glasses");
		setTextureName();
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {

	}
}
