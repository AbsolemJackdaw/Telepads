package net.subaraki.telepads.handler;

import java.util.UUID;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.darkhax.bookshelf.util.Position;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.subaraki.telepads.handler.PlayerLocations.TelepadEntry;

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
    
    @SubscribeEvent
    public void testEvent (PlayerInteractEvent event) {
        
        // Creates an access point to the players locations.
        PlayerLocations locations = PlayerLocations.getProperties(event.entityPlayer);
        
        if (event.action == Action.RIGHT_CLICK_BLOCK) {
            
            // Grabs the players list of entries, and adds a new one.
            locations.getEntries().add(new TelepadEntry(UUID.randomUUID().toString(), event.entityPlayer.dimension, new Position(event.entityPlayer)));
            
            // Can be called whenever you want. This will ensure that the client and server
            // versions of this location instance are 100% synced up to this point.
            locations.sync();
            
            if (event.entityPlayer.worldObj.isRemote)
                System.out.println("Client: " + locations.getEntries().toString());
                
            else
                System.out.println("Server: " + locations.getEntries().toString());
        }
    }
}