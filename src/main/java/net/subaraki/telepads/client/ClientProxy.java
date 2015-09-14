package net.subaraki.telepads.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.subaraki.telepads.Telepads;
import net.subaraki.telepads.client.renderer.RenderItemTelepad;
import net.subaraki.telepads.client.renderer.RenderTileEntityTelepad;
import net.subaraki.telepads.common.CommonProxy;
import net.subaraki.telepads.tileentity.TileEntityTelepad;

public class ClientProxy extends CommonProxy {
    
    @Override
    public void preInit () {
        
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Telepads.blockPad), new RenderItemTelepad());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTelepad.class, new RenderTileEntityTelepad());
    }
}
