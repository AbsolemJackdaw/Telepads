package net.subaraki.telepads.client.renderer;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.subaraki.telepads.client.model.ModelTelepad;
import net.subaraki.telepads.tileentity.TileEntityTelepad;

public class RenderItemTelepad implements IItemRenderer {
    private ModelTelepad pad;
    
    private static ResourceLocation loc = new ResourceLocation("telepads:textures/telepad.png");
    
    public RenderItemTelepad() {
        pad = new ModelTelepad();
    }
    
    @Override
    public boolean handleRenderType (ItemStack item, ItemRenderType type) {
        
        return true;
    }
    
    @Override
    public void renderItem (ItemRenderType type, ItemStack item, Object... data) {
        
        TileEntityRendererDispatcher.instance.renderTileEntityAt(new TileEntityTelepad(), 0.0D, 0.0D, 0.0D, 0.0F);
        
    }
    
    @Override
    public boolean shouldUseRenderHelper (ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        
        return true;
    }
}
