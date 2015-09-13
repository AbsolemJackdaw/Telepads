package net.subaraki.telepads;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;
import net.subaraki.telepads.common.CommonProxy;
import net.subaraki.telepads.common.network.PacketSyncTelepadEntries;
import net.subaraki.telepads.handler.ConfigurationHandler;
import net.subaraki.telepads.handler.GuiHandler;
import net.subaraki.telepads.handler.PlayerHandler;
import net.subaraki.telepads.util.Constants;

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
    
    @EventHandler
    public void preInit (FMLPreInitializationEvent event) {
        
        network = NetworkRegistry.INSTANCE.newSimpleChannel("Telepads");
        network.registerMessage(PacketSyncTelepadEntries.PacketSyncPositionsHandler.class, PacketSyncTelepadEntries.class, 0, Side.CLIENT);
        
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        new ConfigurationHandler(event.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(new PlayerHandler());
        
        proxy.preInit();
    }
    
    @EventHandler
    public void init (FMLInitializationEvent event) {
        
        proxy.init();
    }
    
    @EventHandler
    public void onPostInit (FMLPostInitializationEvent event) {
        
        proxy.postInit();
    }
}