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
    
    @SubscribeEvent
    public void testEvent (PlayerInteractEvent event) {
        
        List<Position> positions = PlayerLocationProperties.getProperties(event.entityPlayer).getPositions();
        
        
        if (event.action == Action.RIGHT_CLICK_BLOCK) {
            
            if (event.entityPlayer.worldObj.isRemote)
                System.out.println("Client: " + positions.toString());
            
            else 
                System.out.println("Server: " + positions.toString());
            
            Constants.RANDOM.nextInt(1024);
            positions.add(new Position(Constants.RANDOM.nextInt(1024), Constants.RANDOM.nextInt(1024), Constants.RANDOM.nextInt(1024)));
            System.out.println("Position added");
        }
    }
}