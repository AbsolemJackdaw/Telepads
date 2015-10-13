package net.subaraki.telepads.handler;

import java.awt.Color;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.darkhax.bookshelf.util.Position;
import net.darkhax.bookshelf.util.Utilities;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.subaraki.telepads.Telepads;
import net.subaraki.telepads.blocks.BlockTelepad;
import net.subaraki.telepads.handler.PlayerLocations.TelepadEntry;
import net.subaraki.telepads.tileentity.TileEntityTelepad;

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
    public void onBucketEvent (net.minecraftforge.event.entity.player.FillBucketEvent event) {
        
        if (event.current != null)
            if (event.current.getItem().equals(Items.water_bucket)) {
                if (event.target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    Block b = event.world.getBlock(event.target.blockX, event.target.blockY, event.target.blockZ);
                    
                    if (b instanceof BlockTelepad) {
                        TileEntityTelepad telepad = (TileEntityTelepad) event.world.getTileEntity(event.target.blockX, event.target.blockY, event.target.blockZ);
                        
                        if (event.entityPlayer.isSneaking())
                            telepad.setBaseColor(new Color(243, 89, 233).getRGB());
                        if (!event.entityPlayer.isSneaking())
                            telepad.setFrameColor(new Color(26, 246, 172).getRGB());
                            
                        telepad.markDirty();
                        
                        if (!event.entityPlayer.capabilities.isCreativeMode)
                            event.entityPlayer.setCurrentItemOrArmor(0, new ItemStack(Items.bucket, 1));
                            
                        event.setCanceled(true);
                    }
                }
                
            }
    }
    
    @SubscribeEvent
    public void onPlayerInteraction (PlayerInteractEvent event) {
        
        if (event.action.equals(Action.RIGHT_CLICK_BLOCK) && event.world.getBlock(event.x, event.y, event.z) instanceof BlockTelepad) {
            
            TileEntityTelepad telepad = (TileEntityTelepad) event.world.getTileEntity(event.x, event.y, event.z);
            int itemColor = Utilities.getDyeColor(event.entityPlayer.getHeldItem());
            
            if (itemColor != -1337) {
                
                if (event.entityPlayer.isSneaking())
                    telepad.setBaseColor(itemColor);
                    
                else
                    telepad.setFrameColor(itemColor);
                    
                telepad.markDirty();
                
                if (!event.entityPlayer.capabilities.isCreativeMode)
                    event.entityPlayer.getHeldItem().stackSize--;
                    
                event.entityPlayer.swingItem();
            }
            
            if (event.entityPlayer.getHeldItem() != null) {
                
                if (event.entityPlayer.getHeldItem().getItem().equals(Telepads.transmitter) && !telepad.hasDimensionUpgrade()) {
                    
                    PlayerLocations playerLocations = PlayerLocations.getProperties(event.entityPlayer);
                    
                    for (TelepadEntry tpe : playerLocations.getEntries())
                        if (tpe.position.equals(new Position(event.x, event.y, event.z)))
                            if (tpe.dimensionID == telepad.getWorldObj().provider.dimensionId)
                                tpe.setTransmitter(true);
                                
                    telepad.addDimensionUpgrade();
                    if (!event.entityPlayer.capabilities.isCreativeMode)
                        event.entityPlayer.getHeldItem().stackSize--;
                        
                    telepad.markDirty();
                }
                else if (event.entityPlayer.getHeldItem().getItem().equals(Telepads.toggler)) {
                    
                    PlayerLocations playerLocations = PlayerLocations.getProperties(event.entityPlayer);
                    
                    telepad.addRedstoneUpgrade();
                    if (!event.entityPlayer.capabilities.isCreativeMode)
                        event.entityPlayer.getHeldItem().stackSize--;
                        
                    telepad.markDirty();
                }
            }
        }
    }
}