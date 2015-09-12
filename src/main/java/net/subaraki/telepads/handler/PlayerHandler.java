package net.subaraki.telepads.handler;

import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.darkhax.bookshelf.util.Constants;
import net.darkhax.bookshelf.util.Position;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public class PlayerHandler {
    
    @SubscribeEvent
    public void onPlayerClone (PlayerEvent.Clone event) {
        
        PlayerLocationProperties.getProperties(event.entityPlayer).copy(PlayerLocationProperties.getProperties(event.original));
    }
    
    @SubscribeEvent
    public void onEntityConstructing (EntityConstructing event) {
        
        if (event.entity instanceof EntityPlayer && !PlayerLocationProperties.hasProperties((EntityPlayer) event.entity))
            PlayerLocationProperties.setProperties((EntityPlayer) event.entity);
    }
}