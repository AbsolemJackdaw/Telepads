package telepads.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemArchimedesLever extends Item{

	public ItemArchimedesLever() {
		super();
		setCreativeTab(CreativeTabs.tabTools);
		setUnlocalizedName("crowbar");
		setTextureName("minecraft:stick");
		setFull3D();
		setMaxStackSize(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack is, int par2) {
		return 0x20b2aa;
	}
}
