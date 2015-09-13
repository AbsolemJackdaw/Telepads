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

		ByteBufUtils.writeVarInt(buf, pos.getX(), 32);
		ByteBufUtils.writeVarInt(buf, pos.getY(), 32);
		ByteBufUtils.writeVarInt(buf, pos.getZ(), 32);
		ByteBufUtils.writeVarInt(buf, dimension, 8);

	}

	public static class PacketTeleportHandler implements IMessageHandler<PacketTeleport, IMessage>{

		@Override
		public IMessage onMessage(PacketTeleport message, MessageContext ctx) {
			
			EntityPlayer player = ctx.getServerHandler().playerEntity;

			int dimID = message.dimension;

			int otherX = message.pos.getX();
			int otherY = message.pos.getY();
			int otherZ = message.pos.getZ();

			if (dimID != player.worldObj.provider.dimensionId) {
				if (player.worldObj.provider.dimensionId != 1) {
					player.travelToDimension(dimID);
					player.setPositionAndUpdate(otherX + 1.5d, otherY + 0.5d,otherZ);
				}
			} else
				player.setPositionAndUpdate(otherX + 1.5d, otherY + 0.5d,otherZ);

			
			return null;
		}
	}

}
