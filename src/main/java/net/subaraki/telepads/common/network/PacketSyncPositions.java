package net.subaraki.telepads.common.network;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.util.Position;
import net.darkhax.bookshelf.util.Utilities;
import net.minecraft.entity.player.EntityPlayer;
import net.subaraki.telepads.handler.PlayerLocationProperties;

public class PacketSyncPositions implements IMessage {
    
    private UUID playerUUID;
    private List<Position> positions;
    
    public PacketSyncPositions() {
    
    }
    
    public PacketSyncPositions(UUID playerUUID, List<Position> positions) {
        
        this.playerUUID = playerUUID;
        this.positions = positions;
    }
    
    @Override
    public void fromBytes (ByteBuf buf) {
        
        List<Position> positions = new ArrayList<Position>();
        this.playerUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        int size = buf.readInt();
        
        if (!(size > 0))
            return;
            
        for (int index = 0; index < size; index++)
            positions.add(new Position(buf));
            
        this.positions = positions;
    }
    
    @Override
    public void toBytes (ByteBuf buf) {
        
        ByteBufUtils.writeUTF8String(buf, playerUUID.toString());
        buf.writeInt(this.positions.size());
        
        for (Position pos : this.positions)
            pos.write(buf);
    }
    
    public static class PacketSyncPositionsHandler implements IMessageHandler<PacketSyncPositions, IMessage> {
        
        @Override
        public IMessage onMessage (PacketSyncPositions packet, MessageContext ctx) {
            
            EntityPlayer player = Utilities.getPlayerFromUUID(Utilities.thePlayer().worldObj, packet.playerUUID);
            PlayerLocationProperties.getProperties(player).setPositions(packet.positions);
            return null;
        }
    }
}