package net.creeperhost.modjam4.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.creeperhost.modjam4.reference.ItemInfo;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.util.EnumHelper;

public class ModItems {
	public static ItemGlasses glasses;
	public static ItemGlassesSide glassesSide;

	//TODO: This should probably be difined in another class, possibly the glasses one
	private static ItemArmor.ArmorMaterial glassesMaterial = EnumHelper.addArmorMaterial("glassesMaterial", 1, new int[]{0, 0, 0, 0}, 0);

	public static void init() {
		glasses = new ItemGlasses(glassesMaterial, 0);
		glassesSide = new ItemGlassesSide();

		GameRegistry.registerItem(glasses, ItemInfo.GLASSES_NAME);
		GameRegistry.registerItem(glassesSide, ItemInfo.GLASSES_SIDE_NAME);
	}
}
