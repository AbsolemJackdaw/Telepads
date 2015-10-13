package net.subaraki.telepads.handler;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.subaraki.telepads.Telepads;

public class RecipeHandler {
    
    public static void initBlockRecipes () {
        
        GameRegistry.addRecipe(new ItemStack(Telepads.blockPad, 1), new Object[] { "BBB", "EEE", "IGI", 'B', Blocks.glass, 'E', Items.ender_pearl, 'G', Items.gold_ingot, 'I', Blocks.iron_block });
    }
    
    public static void initItemRecipes () {
        
        GameRegistry.addRecipe(new ItemStack(Telepads.transmitter, 1), new Object[] { "III", "RDR", "III", 'I', Items.iron_ingot, 'R', Items.redstone, 'D', Items.diamond });
        
        GameRegistry.addRecipe(new ItemStack(Telepads.toggler, 1), new Object[] { "TRT", "RGR", "TRT", 'R', Items.redstone, 'T', Blocks.redstone_torch, 'G', Items.gold_ingot });
    }
    
}
