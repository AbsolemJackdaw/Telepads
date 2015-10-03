package net.subaraki.telepads.items;

import scala.reflect.internal.Constants.Constant;
import net.darkhax.bookshelf.util.Utilities;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.subaraki.telepads.Telepads;
import net.subaraki.telepads.util.Constants;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTransmitter extends Item {

	public ItemTransmitter() {
		this.setUnlocalizedName("telepad.transmitter");
		this.setCreativeTab(CreativeTabs.tabMisc);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons (IIconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon("telepads:transmitter");
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player) {

		player.openGui(Telepads.instance, Constants.GUI_ID_REMOVEPAD, world, (int)player.posX, (int)player.posY, (int)player.posZ);
		
		
		return super.onItemRightClick(is, world, player);
	}
}
