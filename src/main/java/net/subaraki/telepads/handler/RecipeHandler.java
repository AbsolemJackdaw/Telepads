package net.subaraki.telepads.handler;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.subaraki.telepads.Telepads;
import cpw.mods.fml.common.registry.GameRegistry;

public class RecipeHandler {

	public static void initBlockRecipes () {

		GameRegistry.addRecipe(new ItemStack(Telepads.blockPad, 1), new Object[] {});
	}

	public static void initItemRecipes () {

		GameRegistry.addRecipe(new ItemStack(Telepads.transmitter, 1), new Object[] { "I", "I", "I", "R", "D", "R", "I", "I", "I", 
			Character.valueOf('I'), Items.iron_ingot, Character.valueOf('R'), Items.redstone, Character.valueOf('D'), Items.diamond,});

		GameRegistry.addRecipe(new ItemStack(Telepads.toggler, 1), new Object[] { "T", "R", "T", "R", "G", "R", "T", "R", "T", 
			Character.valueOf('R'), Items.redstone, Character.valueOf('T'), Blocks.redstone_torch, Character.valueOf('G'), Items.gold_ingot});

		GameRegistry.addRecipe(new ItemStack(Telepads.toggler, 1), new Object[] { "G", "G", "G", "E", "E", "E", "I", "G", "I", 
			Character.valueOf('G'), Blocks.glass, Character.valueOf('E'), Items.ender_eye, Character.valueOf('G'), Blocks.gold_block,
			Character.valueOf('I'), Blocks.iron_block});

	}

}
