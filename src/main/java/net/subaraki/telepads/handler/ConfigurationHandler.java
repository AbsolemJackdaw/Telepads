package net.subaraki.telepads.handler;

import java.io.File;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;
import net.subaraki.telepads.util.Constants;

public class ConfigurationHandler {
    
    public static Configuration config;
    
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
        
        if (config.hasChanged())
            config.save();
    }
}