package net.subaraki.telepads.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class PlayerHandler {
    
    @SubscribeEvent
    public void onPlayerClone (PlayerEvent.Clone event) {
        
        PlayerLocations.getProperties(event.entityPlayer).copy(PlayerLocations.getProperties(event.original));
    }
    
    @SubscribeEvent
    public void onEntityConstructing (EntityConstructing event) {
        
        if (event.entity instanceof EntityPlayer && !PlayerLocations.hasProperties((EntityPlayer) event.entity))
            PlayerLocations.setProperties((EntityPlayer) event.entity);
    }
    
    @SubscribeEvent
    public void onEntityJoinWorld (EntityJoinWorldEvent event) {
        
        if (event.entity instanceof EntityPlayer && !event.entity.worldObj.isRemote && PlayerLocations.hasProperties((EntityPlayer) event.entity))
            PlayerLocations.getProperties((EntityPlayer) event.entity).sync();
    }
}