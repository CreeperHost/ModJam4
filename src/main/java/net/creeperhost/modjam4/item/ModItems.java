package net.creeperhost.modjam4.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.creeperhost.modjam4.reference.ItemInfo;
import net.minecraft.item.ItemArmor;

public class ModItems {
	public static ItemGlasses glasses;

	public static void init() {
		glasses = new ItemGlasses(ItemArmor.ArmorMaterial.CHAIN, 0);

		GameRegistry.registerItem(glasses, ItemInfo.GLASSES_NAME);
	}
}
