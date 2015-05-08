package telepads.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketTeleport implements IMessage{

	public PacketTeleport() {
	}

	private int x;
	private int y;
	private int z;
	private int dimension;

	public PacketTeleport(int x, int y, int z, int dim) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.dimension = dim;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		dimension = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(dimension);
	}

	public static class HandlerPacketTeleport implements IMessageHandler<PacketTeleport, IMessage>{

		@Override
		public IMessage onMessage(PacketTeleport message, MessageContext ctx) {

			EntityPlayer player = ctx.getServerHandler().playerEntity;
			
			int dimID = message.dimension;

			int otherX = message.x;
			int otherY = message.y;
			int otherZ = message.z;

			if(dimID != player.worldObj.provider.dimensionId){
				
				if(player.worldObj.provider.dimensionId == 1) {
					player.travelToDimension(1);
				}else{
					player.travelToDimension(dimID);
					player.setPositionAndUpdate(otherX+1.5d, otherY+0.5d, otherZ);
				}

			} else {
				player.setPositionAndUpdate(otherX+1.5d, otherY+0.5d, otherZ);
			}
			return null;
		}
	}
}
