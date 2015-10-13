package net.subaraki.telepads.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemRedstoneUpgrade extends Item {
    
    public ItemRedstoneUpgrade() {
        super();
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.setUnlocalizedName("telepad.toggler");
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IIconRegister iconRegister) {
        
        this.itemIcon = iconRegister.registerIcon("telepads:toggler");
    }
    
}