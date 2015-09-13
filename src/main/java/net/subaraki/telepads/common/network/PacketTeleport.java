package net.subaraki.telepads.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.util.Position;
import net.minecraft.entity.player.EntityPlayer;

public class PacketTeleport implements IMessage {
    
    /**
     * The position to send the player to.
     */
    public Position pos;
    
    /**
     * The dimension to send the player to.
     */
    public int dimension;
    
    /**
     * A packet to teleport the player to a given position from the client side. This packet
     * must be sent from a client thread.
     * 
     * @param pos: The position to send the player to.
     * @param dimension: The dimension to send the player to.
     */
    public PacketTeleport(Position pos, int dimension) {
        
        this.pos = pos;
        this.dimension = dimension;
    }
    
    @Override
    public void fromBytes (ByteBuf buf) {
        
        pos = new Position(buf);
        dimension = buf.readInt();
    }
    
    @Override
    public void toBytes (ByteBuf buf) {
        
        pos.write(buf);
        buf.writeInt(dimension);
    }
    
    public PacketTeleport() {
    
    }
    
    public static class PacketTeleportHandler implements IMessageHandler<PacketTeleport, IMessage> {
        
        @Override
        public IMessage onMessage (PacketTeleport packet, MessageContext ctx) {
            
            EntityPlayer player = ctx.getServerHandler().playerEntity;
            
            if (packet.dimension != player.dimension)
                player.travelToDimension(packet.dimension);
                
            packet.pos.sendEntityToPosition(player);
            return null;
        }
    }
}
