package net.subaraki.telepads.handler;

import java.io.File;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;
import net.subaraki.telepads.util.Constants;

public class ConfigurationHandler {
    
    /**
     * Local instance of the Configuration.
     */
    public static Configuration config;
    
    public static boolean isFluxCapacitorEnabled = false;
    
    /**
     * Constructs the ConfigurationHandler. Only one ConfigurationHandler instance should ever
     * be created.
     * 
     * @param configFile: The file to use for reading/writing configuration data.
     */
    public ConfigurationHandler(File configFile) {
        
        config = new Configuration(configFile);
        FMLCommonHandler.instance().bus().register(this);
        syncConfigData();
    }
    
    @SubscribeEvent
    public void onConfigChange (ConfigChangedEvent.OnConfigChangedEvent event) {
        
        if (event.modID.equals(Constants.MODID))
            syncConfigData();
    }
    
    /**
     * When called, all configurable fields will be reinitialized. For internal use only.
     */
    private void syncConfigData () {
        
        isFluxCapacitorEnabled = config.getBoolean("isFluxEnabled", "secrets", isFluxCapacitorEnabled, "Turns on the flux capacitor. With this, the Telepads can not only transfer through space, but also through time!!!");
        
        if (config.hasChanged())
            config.save();
    }
}