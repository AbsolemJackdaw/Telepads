package net.subaraki.telepads.handler;

import net.darkhax.bookshelf.util.EnumVanillaColors;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.subaraki.telepads.Telepads;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ItemHandler {

	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent event) {
		ItemStack is = event.itemStack;

		if (is.getItem() != null) {
			Block b = Block.getBlockFromItem(event.itemStack.getItem());
			if (b.equals(Telepads.blockPad)) {
				if (is.hasTagCompound()) {

					int i = 0;
					if (is.getTagCompound().hasKey("colorFrame")) {
						for (EnumVanillaColors color : EnumVanillaColors
								.values()) {
							if (color.colorObj.getRGB() == is.getTagCompound()
									.getInteger("colorFrame")) {
								event.toolTip.add("frame color : "
										+ color.colorName);
								break;
							}
							i++;
						}
					}

					if (i == EnumVanillaColors.values().length)
						event.toolTip.add("frame color : " + "none");

					i = 0;

					if (is.getTagCompound().hasKey("colorBase")) {
						for (EnumVanillaColors color : EnumVanillaColors
								.values()) {
							if (color.colorObj.getRGB() == is.getTagCompound()
									.getInteger("colorBase")) {
								event.toolTip.add("base color : "
										+ color.colorName);
								break;
							}
							i++;
						}
					}

					if (i == EnumVanillaColors.values().length)
						event.toolTip.add("frame base : " + "none");
				}
			}
		}
	}
}
