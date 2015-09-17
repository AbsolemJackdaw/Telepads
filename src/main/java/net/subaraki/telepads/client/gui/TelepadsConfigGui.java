package net.subaraki.telepads.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.subaraki.telepads.handler.ConfigurationHandler;
import net.subaraki.telepads.util.Constants;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;

public class TelepadsConfigGui extends GuiConfig {
    
    /**
     * Local instance of the configuration for the mod.
     */
    static Configuration cfg = ConfigurationHandler.config;
    
    public TelepadsConfigGui(GuiScreen parent) {
        
        super(parent, getCategories(), Constants.MODID, false, false, GuiConfig.getAbridgedConfigPath(ConfigurationHandler.config.toString()));
    }
    
    /**
     * Provies a list of all configuration categories as configuration elements.
     * 
     * @return List<IConfigElement>: A list of configuration elements containing every
     *         catagory.
     */
    public static List<IConfigElement> getCategories () {
        
        ArrayList<IConfigElement> elements = new ArrayList<IConfigElement>();
        
        for (String name : cfg.getCategoryNames())
            elements.add(new ConfigElement(cfg.getCategory(name)));
            
        return elements;
    }
}