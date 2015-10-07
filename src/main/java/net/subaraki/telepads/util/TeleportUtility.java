package net.subaraki.telepads.util;

import java.util.Iterator;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.darkhax.bookshelf.util.Position;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.subaraki.telepads.Telepads;

//credit goes to 
//https://github.com/maruohon/enderutilities/blob/master/src/main/java/fi/dy/masa/enderutilities/util/teleport/TeleportEntity.java

//Thanks masa ! 

public class TeleportUtility {
    
    private static Entity teleportEntity (Entity entity, double x, double y, double z, int dimDst, boolean forceRecreate) {
        
        if (entity == null || entity.isDead == true || entity.worldObj.isRemote == true) {
            return null;
        }
        
        // Post the event and check if the teleport should be allowed
        if (entity instanceof EntityLivingBase) {
            EnderTeleportEvent etpEvent = new EnderTeleportEvent((EntityLivingBase) entity, x, y, z, 0.0f);
            if (MinecraftForge.EVENT_BUS.post(etpEvent) == true) {
                return null;
            }
        }
        
        if (entity.worldObj.isRemote == false && entity.worldObj instanceof WorldServer) {
            MinecraftServer minecraftserver = MinecraftServer.getServer();
            WorldServer worldServerDst = minecraftserver.worldServerForDimension(dimDst);
            if (worldServerDst == null) {
                Telepads.printDebugMessage("teleportEntity(): worldServerDst == null");
                return null;
            }
            
            IChunkProvider chunkProvider = worldServerDst.getChunkProvider();
            if (chunkProvider == null)
                return null;
                
            if (chunkProvider.chunkExists((int) x >> 4, (int) z >> 4) == false) {
                chunkProvider.loadChunk((int) x >> 4, (int) z >> 4);
                
                if (entity instanceof EntityLiving) {
                    ((EntityLiving) entity).setMoveForward(0.0f);
                    ((EntityLiving) entity).getNavigator().clearPathEntity();
                }
                
                if (entity.dimension != dimDst || (entity.worldObj instanceof WorldServer && entity.worldObj != worldServerDst))
                    entity = TeleportUtility.transferEntityToDimension(entity, dimDst, x, y, z);
                else {
                    if (entity instanceof EntityPlayer)
                        ((EntityPlayer) entity).setPositionAndUpdate(x, y, z);
                    // Forcing a recreate even in the same dimension, mainly
                    // used when teleporting mounted entities where a player is
                    // one of them
                    else if (forceRecreate == true)
                        entity = TeleportUtility.reCreateEntity(entity, x, y, z);
                    else if (entity instanceof EntityLivingBase)
                        ((EntityLivingBase) entity).setPositionAndUpdate(x, y, z);
                    else
                        entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
                }
            }
        }
        
        return entity;
    }
    
    public static Entity reCreateEntity (Entity entitySrc, double x, double y, double z) {
        
        if (entitySrc.worldObj.isRemote == true)
            return null;
            
        WorldServer worldServerDst = MinecraftServer.getServer().worldServerForDimension(entitySrc.dimension);
        if (worldServerDst == null) {
            Telepads.printDebugMessage("reCreateEntity(): worldServerDst == null");
            return null;
        }
        
        Entity entityDst = EntityList.createEntityByName(EntityList.getEntityString(entitySrc), worldServerDst);
        if (entityDst == null)
            return null;
            
        entitySrc.worldObj.removeEntity(entitySrc); // Note: this will also
                                                    // remove any entity mounts
        entitySrc.isDead = false;
        
        entityDst.copyDataFrom(entitySrc, true);
        if (entityDst instanceof EntityLivingBase)
            ((EntityLivingBase) entityDst).setPositionAndUpdate(x, y, z);
        else
            entityDst.setLocationAndAngles(x, y, z, entitySrc.rotationYaw, entitySrc.rotationPitch);
            
        worldServerDst.spawnEntityInWorld(entityDst);
        worldServerDst.resetUpdateEntityTick();
        entitySrc.isDead = true;
        
        return entityDst;
    }
    
    public static Entity transferEntityToDimension (Entity entitySrc, int dimDst, double x, double y, double z) {
        
        if (entitySrc == null || entitySrc.isDead == true || entitySrc.dimension == dimDst || entitySrc.worldObj.isRemote == true)
            return null;
            
        if (entitySrc instanceof EntityPlayerMP)
            return TeleportUtility.transferPlayerToDimension((EntityPlayerMP) entitySrc, dimDst, x, y, z);
            
        WorldServer worldServerSrc = MinecraftServer.getServer().worldServerForDimension(entitySrc.dimension);
        WorldServer worldServerDst = MinecraftServer.getServer().worldServerForDimension(dimDst);
        
        if (worldServerSrc == null || worldServerDst == null) {
            Telepads.printDebugMessage("transferEntityToDimension(): worldServer[Src|Dst] == null");
            return null;
        }
        
        entitySrc.mountEntity((Entity) null);
        if (entitySrc.riddenByEntity != null)
            entitySrc.riddenByEntity.mountEntity((Entity) null);
            
        entitySrc.dimension = dimDst;
        Entity entityDst = EntityList.createEntityByName(EntityList.getEntityString(entitySrc), worldServerDst);
        if (entityDst == null)
            return null;
            
        entityDst.copyDataFrom(entitySrc, true);
        
        x = MathHelper.clamp_double(x, -30000000.0d, 30000000.0d);
        z = MathHelper.clamp_double(z, -30000000.0d, 30000000.0d);
        entityDst.setLocationAndAngles(x, y, z, entitySrc.rotationYaw, entitySrc.rotationPitch);
        worldServerDst.spawnEntityInWorld(entityDst);
        worldServerDst.updateEntityWithOptionalForce(entityDst, false);
        entityDst.setWorld(worldServerDst);
        
        // Debug: this actually kills the original entity, commenting it will
        // make clones
        entitySrc.isDead = true;
        
        worldServerSrc.resetUpdateEntityTick();
        worldServerDst.resetUpdateEntityTick();
        
        return entityDst;
    }
    
    public static EntityPlayerMP transferPlayerToDimension (EntityPlayerMP player, int dimDst, Position pos) {
        
        return transferPlayerToDimension(player, dimDst, pos.getX(), pos.getY(), pos.getZ());
    }
    
    public static EntityPlayerMP transferPlayerToDimension (EntityPlayerMP player, int dimDst, double x, double y, double z) {
        
        if (player == null || player.isDead == true || player.dimension == dimDst || player.worldObj.isRemote == true) {
            return null;
        }
        
        // Post the event and check if the teleport should be allowed
        PlayerChangedDimensionEvent pcdEvent = new PlayerChangedDimensionEvent(player, player.dimension, dimDst);
        if (FMLCommonHandler.instance().bus().post(pcdEvent) == true) {
            return null;
        }
        
        int dimSrc = player.dimension;
        x = MathHelper.clamp_double(x, -30000000.0d, 30000000.0d);
        z = MathHelper.clamp_double(z, -30000000.0d, 30000000.0d);
        player.setLocationAndAngles(x, y, z, player.rotationYaw, player.rotationPitch);
        
        ServerConfigurationManager serverCM = player.mcServer.getConfigurationManager();
        WorldServer worldServerSrc = MinecraftServer.getServer().worldServerForDimension(dimSrc);
        WorldServer worldServerDst = MinecraftServer.getServer().worldServerForDimension(dimDst);
        
        if (worldServerSrc == null || worldServerDst == null) {
            Telepads.printDebugMessage("transferPlayerToDimension(): worldServer[Src|Dst] == null");
            return null;
        }
        
        player.dimension = dimDst;
        player.playerNetServerHandler.sendPacket(new S07PacketRespawn(player.dimension, player.worldObj.difficultySetting, player.worldObj.getWorldInfo().getTerrainType(), player.theItemInWorldManager.getGameType()));
        // worldServerSrc.removePlayerEntityDangerously(player); // this crashes
        worldServerSrc.removeEntity(player);
        player.isDead = false;
        
        player.mountEntity((Entity) null);
        if (player.riddenByEntity != null) {
            player.riddenByEntity.mountEntity((Entity) null);
        }
        
        worldServerDst.spawnEntityInWorld(player);
        worldServerDst.updateEntityWithOptionalForce(player, false);
        player.setWorld(worldServerDst);
        serverCM.func_72375_a(player, worldServerSrc); // remove player from the
                                                       // source world
        player.playerNetServerHandler.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
        player.theItemInWorldManager.setWorld(worldServerDst);
        player.mcServer.getConfigurationManager().updateTimeAndWeatherForPlayer(player, worldServerDst);
        player.mcServer.getConfigurationManager().syncPlayerInventory(player);
        player.addExperience(0);
        player.setPlayerHealthUpdated();
        
        Iterator<PotionEffect> iterator = player.getActivePotionEffects().iterator();
        while (iterator.hasNext()) {
            PotionEffect potioneffect = (PotionEffect) iterator.next();
            player.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), potioneffect));
        }
        
        FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, dimSrc, dimDst);
        
        return player;
    }
}