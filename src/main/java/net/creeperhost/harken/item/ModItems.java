package net.creeperhost.harken.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.creeperhost.harken.reference.ItemInfo;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;

public class ModItems {
	public static ItemGlasses glasses;
	public static ItemGlassesSide glassesSide;
	public static ItemGlassesFront glassesFront;

	//TODO: This should probably be defined in another class, possibly the glasses one
	private static ItemArmor.ArmorMaterial glassesMaterial = EnumHelper.addArmorMaterial("glassesMaterial", 1, new int[]{0, 0, 0, 0}, 0);

	public static void init() {
		createItems();
		registerItems();
	}

	private static void createItems() {
		glasses = new ItemGlasses(glassesMaterial, 0);
		glassesSide = new ItemGlassesSide();
		glassesFront = new ItemGlassesFront();
	}

	private static void registerItems() {
		GameRegistry.registerItem(glasses, ItemInfo.GLASSES_NAME);
		GameRegistry.registerItem(glassesSide, ItemInfo.GLASSES_SIDE_NAME);
		GameRegistry.registerItem(glassesFront, ItemInfo.GLASSES_FRONT_NAME);
	}

	public static void registerRecipes() {
		GameRegistry.addShapedRecipe(new ItemStack(glasses), "SFS", 'S', glassesSide, 'F', glassesFront);
		GameRegistry.addShapedRecipe(new ItemStack(glassesFront), "III", "GGG", 'G', new ItemStack(Blocks.stained_glass_pane, 1, 9), 'I', Items.iron_ingot);
		GameRegistry.addShapedRecipe(new ItemStack(glassesSide, 2), "II ", "  I", 'I', Items.iron_ingot);
	}
}
