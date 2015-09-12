package telepads.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import telepads.util.PlayerPadData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPadLocations extends Item {

	public ItemPadLocations() {
		super();
		setCreativeTab(CreativeTabs.tabTransport);
	}

	private void addInfo(EntityPlayer p, String s) {
		p.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + s));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer par2EntityPlayer,
			List par3List, boolean par4) {

		if (par2EntityPlayer != null) {

			ArrayList<int[]> a = PlayerPadData.get(par2EntityPlayer)
					.getAllCoords();
			ArrayList<Integer> b = PlayerPadData.get(par2EntityPlayer)
					.getAllDims();
			ArrayList<String> c = PlayerPadData.get(par2EntityPlayer)
					.getAllNames();

			for (int i = 0; i < a.size(); i++) {
				par3List.add("x" + a.get(i)[0] + " y" + a.get(i)[1] + " z"
						+ a.get(i)[2] + " " + c.get(i) + " Dim:" + b.get(i));
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack is, int par2) {
		return 0x1af0ab;
	}

	@Override
	public void onCreated(ItemStack is, World par2World,
			EntityPlayer par3EntityPlayer) {
		super.onCreated(is, par2World, par3EntityPlayer);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World par2World,
			EntityPlayer par3EntityPlayer) {

		String a = StatCollector.translateToLocal("info.1");
		String b = StatCollector.translateToLocal("info.2");
		String c = StatCollector.translateToLocal("info.3");
		String d = StatCollector.translateToLocal("info.4");
		String e = StatCollector.translateToLocal("info.5");
		String f = StatCollector.translateToLocal("info.6");
		String g = StatCollector.translateToLocal("info.7");
		String h = StatCollector.translateToLocal("info.8");

		if (!par2World.isRemote) {
			addInfo(par3EntityPlayer, a);
			addInfo(par3EntityPlayer, b);
			addInfo(par3EntityPlayer, c);
			addInfo(par3EntityPlayer, d);
			addInfo(par3EntityPlayer, e);
			addInfo(par3EntityPlayer, f);
			addInfo(par3EntityPlayer, g);
			addInfo(par3EntityPlayer, h);
		}

		return super.onItemRightClick(is, par2World, par3EntityPlayer);
	}

	@Override
	public void onUpdate(ItemStack is, World par2World, Entity ent, int par4,
			boolean par5) {

		super.onUpdate(is, par2World, ent, par4, par5);

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		super.registerIcons(par1IconRegister);
		this.itemIcon = par1IconRegister.registerIcon("map_filled");
	}
}
