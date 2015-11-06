package net.subaraki.telepads.common.network;

import java.util.UUID;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.lib.util.PlayerUtils;
import net.subaraki.telepads.handler.PlayerLocations;
import net.subaraki.telepads.handler.PlayerLocations.TelepadEntry;

public class PacketRemoveTelepadEntry implements IMessage {
    
    /**
     * The UUID of the player to remove the entry from.
     */
    private UUID playerUUID;
    
    /**
     * The entry to be removed from the player's list of locations.
     */
    private TelepadEntry entry;
    
    /**
     * A packet to remove a TelepadEntry from a player's list of locations. This packet is used
     * to send data from the client to the server, and should not be sent from a server thread.
     * When this packet is handled on the server side, a sync packet will automatically be send
     * back to the client to ensure that everthing is consistent.
     * 
     * @param playerUUID : The UUID of the player to remove the entry from.
     * @param entry : The entry to be removed from the player's list of locations.
     */
    public PacketRemoveTelepadEntry(UUID playerUUID, TelepadEntry entry) {
        
        this.playerUUID = playerUUID;
        this.entry = entry;
    }
    
    @Override
    public void fromBytes (ByteBuf buf) {
        
        this.playerUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        this.entry = new TelepadEntry(buf);
    }
    
    @Override
    public void toBytes (ByteBuf buf) {
        
        ByteBufUtils.writeUTF8String(buf, this.playerUUID.toString());
        this.entry.writeToByteBuf(buf);
    }
    
    public PacketRemoveTelepadEntry() {
    
    }
    
    public static class PacketRemoveTelepadEntryHandler implements IMessageHandler<PacketRemoveTelepadEntry, IMessage> {
        
        @Override
        public IMessage onMessage (PacketRemoveTelepadEntry packet, MessageContext ctx) {
            
            PlayerLocations locations = PlayerLocations.getProperties(PlayerUtils.getPlayerFromUUID(ctx.getServerHandler().playerEntity.worldObj, packet.playerUUID));
            locations.removeEntry(packet.entry);
            locations.sync();
            return null;
        }
    }
}