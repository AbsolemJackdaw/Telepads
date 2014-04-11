package telepads;

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
import net.minecraft.world.World;
import telepads.util.PlayerPadData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPadLocations extends Item {

	public ItemPadLocations() {
		super();
		setCreativeTab(CreativeTabs.tabTransport);
	}

	private void addInfo(EntityPlayer p, String s){
		p.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + s));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {

		if(par2EntityPlayer != null){

			ArrayList<int[]> a = PlayerPadData.get(par2EntityPlayer).getAllCoords();
			ArrayList<Integer> b = PlayerPadData.get(par2EntityPlayer).getAllDims();
			ArrayList<String> c = PlayerPadData.get(par2EntityPlayer).getAllNames();

			for(int i = 0; i < a.size(); i++)
				par3List.add("x"+a.get(i)[0]+ " y"+a.get(i)[1]+ " z"+a.get(i)[2]+
						" " + c.get(i) +" Dim:" + b.get(i));
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

		if(!par2World.isRemote){
			addInfo(par3EntityPlayer, "/*======= How To Use =======*/");
			addInfo(par3EntityPlayer, "1.Teleporting : stand on pad and wait 3-5 Seconds.");
			addInfo(par3EntityPlayer, "2.Removing TelePad : Right Click on TelePad");
			addInfo(par3EntityPlayer, "3.Allowing Friends to use your pad : ");
			addInfo(par3EntityPlayer, "3.a : Sneak-right click your pad." );
			addInfo(par3EntityPlayer, "3.b : let friends rightclick pad with register");
			addInfo(par3EntityPlayer, "For security reasons, you can not remove pads that have been registered to friends.");
			addInfo(par3EntityPlayer, "If noone registered, you can set it back to normal by sneak-right clicking it again");

		}
		System.out.println(PlayerPadData.get(par3EntityPlayer).getAllCoords() +
				"\n" + PlayerPadData.get(par3EntityPlayer).getAllDims()+
				"\n" + PlayerPadData.get(par3EntityPlayer).getAllNames());

		return super.onItemRightClick(is, par2World, par3EntityPlayer);
	}

	@Override
	public void onUpdate(ItemStack is, World par2World,
			Entity ent, int par4, boolean par5) {

		super.onUpdate(is, par2World, ent, par4, par5);

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		super.registerIcons(par1IconRegister);
		this.itemIcon = par1IconRegister.registerIcon("map_filled");
	}
}
