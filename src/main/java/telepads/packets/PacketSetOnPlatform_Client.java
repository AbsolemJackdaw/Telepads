package telepads.packets;

import io.netty.buffer.ByteBuf;
import telepads.Telepads;
import telepads.block.TETelepad;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSetOnPlatform_Client implements IMessage {

	public PacketSetOnPlatform_Client() {
	}

	private int x;
	private int y;
	private int z;
	private boolean bool;

	public PacketSetOnPlatform_Client(int x, int y, int z, boolean bool) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.bool = bool;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		bool = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeBoolean(bool);
	}

	public static class HandlerPacketSetOnPlatform_Client implements IMessageHandler<PacketSetOnPlatform_Client, IMessage>{

		@Override
		public IMessage onMessage(PacketSetOnPlatform_Client message, MessageContext ctx) {
			
			TETelepad pad = (TETelepad) Telepads.proxy.getClientPlayer().worldObj.getTileEntity(message.x, message.y, message.z);

			if(pad == null)
				return null;

			pad.setStandingOnPlatform(message.bool);

			return null;
		}
	}

}
