package net.subaraki.telepads.common.network;

import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.util.Position;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketTeleport implements IMessage{

	public Position pos;
	public int dimension;
	
	public PacketTeleport() {
	}
	
	public PacketTeleport(Position pos, int dimension) {
	    
		this.pos = pos;
		this.dimension = dimension;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {

		pos = new Position(buf);
		dimension = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {

	    pos.write(buf);
	    buf.writeInt(dimension);
	}

	public static class PacketTeleportHandler implements IMessageHandler<PacketTeleport, IMessage>{

		@Override
		public IMessage onMessage(PacketTeleport packet, MessageContext ctx) {
			
			EntityPlayer player = ctx.getServerHandler().playerEntity;

			if (packet.dimension != player.dimension)
			    player.travelToDimension(packet.dimension);

			packet.pos.sendEntityToPosition(player);
			return null;
		}
	}
}
