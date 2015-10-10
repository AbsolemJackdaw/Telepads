package net.subaraki.telepads;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.subaraki.telepads.blocks.BlockTelepad;
import net.subaraki.telepads.common.CommonProxy;
import net.subaraki.telepads.common.network.PacketAddTelepadEntry;
import net.subaraki.telepads.common.network.PacketRemoveTelepadEntry;
import net.subaraki.telepads.common.network.PacketSyncPoweredBlock;
import net.subaraki.telepads.common.network.PacketSyncTelepadEntries;
import net.subaraki.telepads.common.network.PacketTeleport;
import net.subaraki.telepads.handler.ConfigurationHandler;
import net.subaraki.telepads.handler.GuiHandler;
import net.subaraki.telepads.handler.ItemHandler;
import net.subaraki.telepads.handler.PlayerHandler;
import net.subaraki.telepads.handler.RecipeHandler;
import net.subaraki.telepads.items.ItemRedstoneUpgrade;
import net.subaraki.telepads.items.ItemTransmitter;
import net.subaraki.telepads.tileentity.TileEntityTelepad;
import net.subaraki.telepads.util.Constants;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = Constants.MODID, name = Constants.MOD_NAME, version = Constants.VERSION, guiFactory = Constants.FACTORY, dependencies = Constants.DEPENDENCY)
public class Telepads {
    
    @SidedProxy(serverSide = Constants.SERVER, clientSide = Constants.CLIENT)
    public static CommonProxy proxy;
    
    @Mod.Instance(Constants.MODID)
    public static Telepads instance;
    
    /**
     * A network channel to be used to handle packets specific to the Telepads mod.
     */
    public SimpleNetworkWrapper network;
    
    public static Block blockPad = new BlockTelepad();
    public static Item transmitter = new ItemTransmitter();
    public static Item toggler = new ItemRedstoneUpgrade();

    @EventHandler
    public void preInit (FMLPreInitializationEvent event) {
        
        network = NetworkRegistry.INSTANCE.newSimpleChannel("Telepads");
        network.registerMessage(PacketSyncTelepadEntries.PacketSyncTelepadEntriesHandler.class, PacketSyncTelepadEntries.class, 0, Side.CLIENT);
        network.registerMessage(PacketAddTelepadEntry.PacketAddTelepadEntryHandler.class, PacketAddTelepadEntry.class, 1, Side.SERVER);
        network.registerMessage(PacketRemoveTelepadEntry.PacketRemoveTelepadEntryHandler.class, PacketRemoveTelepadEntry.class, 2, Side.SERVER);
        network.registerMessage(PacketTeleport.PacketTeleportHandler.class, PacketTeleport.class, 3, Side.SERVER);
        network.registerMessage(PacketSyncPoweredBlock.PacketSyncPoweredBlockHandler.class, PacketSyncPoweredBlock.class, 4, Side.CLIENT);
        
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        
        new ConfigurationHandler(event.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(new PlayerHandler());
        MinecraftForge.EVENT_BUS.register(new ItemHandler());
        
        GameRegistry.registerBlock(blockPad, "telepad");
        GameRegistry.registerTileEntity(TileEntityTelepad.class, "TETelepad");
        
        GameRegistry.registerItem(transmitter, "Transmitter Upgrade");
        GameRegistry.registerItem(toggler, "Toggler Upgrade");

        proxy.preInit();
    }
    
    @EventHandler
    public void init (FMLInitializationEvent event) {
    	RecipeHandler.initBlockRecipes();
        RecipeHandler.initItemRecipes();
    }
    
    /**
     * Prints a specified message to the console. If the user has disabled debug messages,
     * messages sent through this method will not be logged.
     * 
     * @param message : The message to send to the console.
     */
    public static void printDebugMessage (String message) {
        
        if (ConfigurationHandler.allowDebugMessages)
            Constants.LOG.info(message);
    }
}