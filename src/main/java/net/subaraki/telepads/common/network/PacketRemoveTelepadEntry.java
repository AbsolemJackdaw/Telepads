package net.subaraki.telepads.common.network;

import java.util.UUID;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.util.Utilities;
import net.subaraki.telepads.handler.PlayerLocations;
import net.subaraki.telepads.handler.PlayerLocations.TelepadEntry;

public class PacketRemoveTelepadEntry implements IMessage {
    
    private UUID playerUUID;
    private TelepadEntry entry;
    
    public PacketRemoveTelepadEntry() {
    
    }
    
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
    
    public static class PacketRemoveTelepadEntryHandler implements IMessageHandler<PacketRemoveTelepadEntry, IMessage> {
        
        @Override
        public IMessage onMessage (PacketRemoveTelepadEntry packet, MessageContext ctx) {
            
            PlayerLocations locations = PlayerLocations.getProperties(Utilities.getPlayerFromUUID(ctx.getServerHandler().playerEntity.worldObj, packet.playerUUID));
            locations.removeEntry(packet.entry);
            locations.sync();
            return null;
        }
    }
}