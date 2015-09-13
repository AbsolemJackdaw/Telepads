package net.subaraki.telepads.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import cpw.mods.fml.common.registry.GameRegistry;

public class BlockHandling {

	public static TelePadBlock blockPad;
	
	
	public static void registerBlocks(){
		
		blockPad = (TelePadBlock) new TelePadBlock(Material.wood).setBlockName("telepad").setLightLevel(0.2f)
				.setCreativeTab(CreativeTabs.tabTransport).setBlockUnbreakable().setBlockTextureName("wool_colored_pink");
		
		GameRegistry.registerBlock(BlockHandling.blockPad, "TelePad");
	}
	
}
