package telepads.packets;

import io.netty.buffer.ByteBuf;
import telepads.Telepads;
import telepads.util.PlayerPadData;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketDataOverDimensions implements IMessage {

	public PacketDataOverDimensions() {
	}
	
	private int x;
	private int y;
	private int z;
	private int dimension;
	private String name;
	
	public PacketDataOverDimensions(int x, int y, int z, int dimension, String name) {
		
		this.x = x;
		this.y = y;
		this.z = z;
		this.dimension = dimension;
		this.name = name;
		
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		dimension = buf.readInt();
		name = ByteBufUtils.readUTF8String(buf);
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(dimension);
		ByteBufUtils.writeUTF8String(buf, name);
		
	}
	
	public static class HandlerPacketDataOverDimensions implements IMessageHandler<PacketDataOverDimensions, IMessage>{

		@Override
		public IMessage onMessage(PacketDataOverDimensions message, MessageContext ctx) {
			
			PlayerPadData dat = PlayerPadData.get(Telepads.proxy.getClientPlayer());
			
			int[] coords = new int[]{message.x, message.y, message.z};

			dat.getAllCoords().add(coords);
			dat.getAllDims().add(message.dimension);
			dat.getAllNames().add(message.name);
			return null;
		}
	}
}
