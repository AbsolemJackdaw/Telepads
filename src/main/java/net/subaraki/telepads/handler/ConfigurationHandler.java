package net.subaraki.telepads.handler;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.subaraki.telepads.util.Constants;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ConfigurationHandler {
    
    /**
     * Local instance of the Configuration.
     */
    public static Configuration config;
    
    /**
     * A configurable flag that determines whether or not the user wants to receive debug
     * messages from the Telepads mod.
     */
    public static boolean allowDebugMessages = true;
    
    /**
     * A configurable flag that determines whether or not the Telepad will spawn particle
     * effects.
     */
    public static boolean allowParticleEffects = true;
    
    /**
     * A configurable string that determines the type of particle that is spawned by the
     * Telepad.
     */
    public static String particleName = "portal";
    
    /**
     * Constructs the ConfigurationHandler. Only one ConfigurationHandler instance should ever
     * be created.
     * 
     * @param configFile : The file to use for reading/writing configuration data.
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
        
        // Settings
        allowDebugMessages = config.getBoolean("allowDebug", "settings", allowDebugMessages, "Determines whether or not dubug messages from the Telepads mod should be printed to the console.");
        allowParticleEffects = config.getBoolean("allowParticles", "settings", allowParticleEffects, "Should particle effects be spawned from the Telepad?");
        particleName = config.getString("particleType", "settings", particleName, "The type of particle that should spawn from the Telepad?");
        
        if (config.hasChanged())
            config.save();
    }
}