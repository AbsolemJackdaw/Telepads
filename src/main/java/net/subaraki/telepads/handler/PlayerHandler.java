package net.subaraki.telepads.handler;

import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.darkhax.bookshelf.util.Constants;
import net.darkhax.bookshelf.util.Position;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

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
        
        if (event.entity instanceof EntityPlayer && PlayerLocations.hasProperties((EntityPlayer) event.entity))
            PlayerLocations.getProperties((EntityPlayer) event.entity).sync();
    }
    
    @SubscribeEvent
    public void testEvent (PlayerInteractEvent event) {
        
        List<Position> positions = PlayerLocations.getProperties(event.entityPlayer).getPositions();
        
        if (event.action == Action.RIGHT_CLICK_BLOCK) {
            
            int x = Constants.RANDOM.nextInt(1024);
            int y = Constants.RANDOM.nextInt(1024);
            int z = Constants.RANDOM.nextInt(1024);
            
            positions.add(new Position(x, y, z));
            
            if (event.entityPlayer.worldObj.isRemote)
                System.out.println("Client: " + positions.toString());
                
            else
                System.out.println("Server: " + positions.toString());
                
            System.out.println("Position added");
        }
    }
    
    public static String gggg (List<Position> poss) {
        
        String output = "";
        for (Position pos : poss)
            output = pos.toString() + " ";
            
        return output;
    }
}