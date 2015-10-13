package net.subaraki.telepads.client.renderer;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
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
        
        TileEntitySpecialRenderer tesr = TileEntityRendererDispatcher.instance.getSpecialRenderer(new TileEntityTelepad());
        
        if (tesr instanceof RenderTileEntityTelepad) {
            
            RenderTileEntityTelepad rtet = (RenderTileEntityTelepad) tesr;
            
            if (item.hasTagCompound()) {
                
                if (item.getTagCompound().hasKey("colorFrame") && item.getTagCompound().hasKey("colorBase")) {
                    
                    GL11.glPushMatrix();
                    
                    GL11.glTranslatef((float) 0.5F, (float) 2.25F, (float) 0.5F);
                    GL11.glScalef(1.0F, -1F, -1F);
                    rtet.renderPad(new TileEntityTelepad(), new Color(item.getTagCompound().getInteger("colorFrame")), new Color(item.getTagCompound().getInteger("colorBase")), 0, 0, 0, 0);
                    
                    GL11.glPopMatrix();
                }
            }
            else {
                GL11.glPushMatrix();
                
                GL11.glTranslatef((float) 0.5F, (float) 2.25F, (float) 0.5F);
                GL11.glScalef(1.0F, -1F, -1F);
                rtet.renderPad(new TileEntityTelepad(), new Color(26, 246, 172), new Color(243, 89, 233), 0, 0, 0, 0);
                
                GL11.glPopMatrix();
            }
        }
    }
    
    @Override
    public boolean shouldUseRenderHelper (ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        
        return true;
    }
}
