package net.subaraki.telepads.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.darkhax.bookshelf.lib.VanillaColor;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.subaraki.telepads.Telepads;

public class ItemHandler {
    
    @SubscribeEvent
    public void onTooltip (ItemTooltipEvent event) {
        
        ItemStack is = event.itemStack;
        
        if (is.getItem() != null) {
            Block b = Block.getBlockFromItem(event.itemStack.getItem());
            if (b.equals(Telepads.blockPad)) {
                if (is.hasTagCompound()) {
                    
                    int i = 0;
                    if (is.getTagCompound().hasKey("colorFrame")) {
                        for (VanillaColor color : VanillaColor.values()) {
                            if (color.color.getRGB() == is.getTagCompound().getInteger("colorFrame")) {
                                event.toolTip.add("frame color : " + color.name);
                                break;
                            }
                            i++;
                        }
                    }
                    
                    if (i == VanillaColor.values().length)
                        event.toolTip.add("frame color : " + "none");
                        
                    i = 0;
                    
                    if (is.getTagCompound().hasKey("colorBase")) {
                        for (VanillaColor color : VanillaColor.values()) {
                            if (color.color.getRGB() == is.getTagCompound().getInteger("colorBase")) {
                                event.toolTip.add("base color : " + color.name);
                                break;
                            }
                            i++;
                        }
                    }
                    
                    if (i == VanillaColor.values().length)
                        event.toolTip.add("frame base : " + "none");
                }
            }
        }
    }
}
