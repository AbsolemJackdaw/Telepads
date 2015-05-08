package telepads;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import telepads.block.BlockTelepad;
import telepads.block.TETelepad;
import telepads.item.ItemArchimedesLever;
import telepads.item.ItemPadLocations;
import telepads.packets.PacketAddTelepadForPlayer;
import telepads.packets.PacketAddTelepadForPlayer.HandlerPacketAddTelepadForPlayer;
import telepads.packets.PacketAddTelepadForPlayer_Client;
import telepads.packets.PacketAddTelepadForPlayer_Client.HandlerPacketAddTelepadForPlayer_Client;
import telepads.packets.PacketDataOverDimensions;
import telepads.packets.PacketDataOverDimensions.HandlerPacketDataOverDimensions;
import telepads.packets.PacketSetOnPlatform_Client;
import telepads.packets.PacketSetOnPlatform_Client.HandlerPacketSetOnPlatform_Client;
import telepads.packets.PacketTeleport;
import telepads.packets.PacketTeleport.HandlerPacketTeleport;
import telepads.util.DataTracker;
import telepads.util.PadEvents;
import telepads.util.TelePadGuiHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = Telepads.modID, name = Telepads.modName, version = Telepads.version)
public class Telepads {

	protected static final String version = "1.7.10 v1";
	protected static final String modID = "telepads";
	protected static final String modName = "Teleportation Pads";

	public static BlockTelepad telepad;
	public static ItemPadLocations register;
	public static ItemArchimedesLever crowbar;

	@SidedProxy(serverSide = "telepads.TelepadProxyServer", clientSide = "telepads.TelepadProxyClient")
	public static TelepadProxyServer proxy;
	public static Telepads instance;

	//	public static FMLEventChannel Channel;
	//	public static final String packetChannel = "TelepadPackets";

	public static final SimpleNetworkWrapper SNW = NetworkRegistry.INSTANCE.newSimpleChannel("TelepadPackets");

	@EventHandler
	public void load(FMLInitializationEvent evt){

		instance = this;

		GameRegistry.addRecipe(new ItemStack(telepad,1),new Object[] {"GGG", "RER", "RIR",
			'G', Blocks.glass, 'R', Items.redstone, 'E', Items.ender_pearl, 'I', Blocks.iron_block});

		GameRegistry.addRecipe(new ItemStack(crowbar,1),new Object[] {"  I"," S ","I  ",'I',Items.iron_ingot,'S',Items.stick}) ;

		GameRegistry.addShapelessRecipe(new ItemStack(register,1), Items.paper,Items.paper, Items.feather, new ItemStack(Items.dye,1,0));

		proxy.registerTileEntity();
		GameRegistry.registerTileEntity(TETelepad.class, "TETelepad");
		proxy.registerItemRenderer();

		proxy.registerPacketHandlers();

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new TelePadGuiHandler());
		FMLCommonHandler.instance().bus().register(new DataTracker());
		MinecraftForge.EVENT_BUS.register(new PadEvents());
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent e){

		register = (ItemPadLocations) new ItemPadLocations().setUnlocalizedName("register");
		GameRegistry.registerItem(register, "ItemPadLocations");

		crowbar = (ItemArchimedesLever) new ItemArchimedesLever().setUnlocalizedName("Crowbar");
		GameRegistry.registerItem(crowbar ,"Archimede's Lever" );

		telepad = (BlockTelepad) new BlockTelepad(Material.wood).setBlockName("telepad").setLightLevel(0.2f).setCreativeTab(CreativeTabs.tabTransport).setBlockUnbreakable().setBlockTextureName("wool_colored_pink");
		GameRegistry.registerBlock(telepad, "TelePad");
		
		SNW.registerMessage(HandlerPacketAddTelepadForPlayer.class, PacketAddTelepadForPlayer.class, 1, Side.SERVER);
//		SNW.registerMessage(HandlerPacketSetOnPlatform_Server.class, PacketSetOnPlatform_Server.class, 2, Side.SERVER);
		SNW.registerMessage(HandlerPacketTeleport.class, PacketTeleport.class, 3, Side.SERVER);
		
		SNW.registerMessage(HandlerPacketDataOverDimensions.class, PacketDataOverDimensions.class, 4, Side.CLIENT);
		SNW.registerMessage(HandlerPacketAddTelepadForPlayer_Client.class, PacketAddTelepadForPlayer_Client.class, 5, Side.CLIENT);
		SNW.registerMessage(HandlerPacketSetOnPlatform_Client.class, PacketSetOnPlatform_Client.class, 6, Side.CLIENT);

	}
}
