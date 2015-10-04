package net.subaraki.telepads.handler;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.subaraki.telepads.Telepads;
import cpw.mods.fml.common.registry.GameRegistry;

public class RecipeHandler {

	public static void initBlockRecipes() {

		GameRegistry.addRecipe(new ItemStack(Telepads.blockPad, 1),
				new Object[] {});
	}

	public static void initItemRecipes() {

		GameRegistry.addRecipe(
				new ItemStack(Telepads.transmitter, 1),
				new Object[] { "I", "I", "I", "R", "D", "R", "I", "I", "I",
						Character.valueOf('I'), Items.iron_ingot,
						Character.valueOf('R'), Items.redstone,
						Character.valueOf('D'), Items.diamond,

				});

	}

}
