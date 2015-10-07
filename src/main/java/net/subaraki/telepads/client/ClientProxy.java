package net.subaraki.telepads.client;

import java.util.Random;

import net.darkhax.bookshelf.util.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.subaraki.telepads.Telepads;
import net.subaraki.telepads.client.renderer.RenderItemTelepad;
import net.subaraki.telepads.client.renderer.RenderTileEntityTelepad;
import net.subaraki.telepads.common.CommonProxy;
import net.subaraki.telepads.handler.ConfigurationHandler;
import net.subaraki.telepads.tileentity.TileEntityTelepad;
import cpw.mods.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {
    
    @Override
    public void preInit () {
        
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Telepads.blockPad), new RenderItemTelepad());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTelepad.class, new RenderTileEntityTelepad());
    }
    
    @Override
    public void createTelepadParticleEffect (int x, int y, int z, boolean isStandingOnPlate) {
        
        World world = Minecraft.getMinecraft().theWorld;
        
        if (world == null || !ConfigurationHandler.allowParticleEffects)
            return;
            
        Random rand = Constants.RANDOM;
        int maxParticleCount = (isStandingOnPlate) ? 100 : 5;
        
        for (int particleCount = 0; particleCount < maxParticleCount; ++particleCount) {
            
            double posX = x + 0.5f;
            double posY = y + (rand.nextFloat() * 1.5f);
            double posZ = z + 0.5f;
            double velocityX = 0.0D;
            double volocityY = 0.0D;
            double velocityZ = 0.0D;
            int velocityXOffset = (rand.nextInt(2) * 2) - 1;
            int velocityZOffset = (rand.nextInt(2) * 2) - 1;
            
            velocityX = (rand.nextFloat() - 0.5D) * 0.125D;
            volocityY = (rand.nextFloat() - 0.5D) * 0.125D;
            velocityZ = (rand.nextFloat() - 0.5D) * 0.125D;
            velocityX = rand.nextFloat() * 1.0F * velocityXOffset;
            velocityZ = rand.nextFloat() * 1.0F * velocityZOffset;
            world.spawnParticle(ConfigurationHandler.particleName, posX, posY, posZ, velocityX, volocityY, velocityZ);
        }
    }
    
    public World getClientWorld () {
        
        return Minecraft.getMinecraft().theWorld;
    }
}
