package telepads.packets;

import io.netty.buffer.ByteBuf;
import telepads.Telepads;
import telepads.block.TETelepad;
import telepads.util.PlayerPadData;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketAddTelepadForPlayer_Client implements IMessage {

	public PacketAddTelepadForPlayer_Client() {
	}

	int x;
	int y;
	int z;
	String name;

	public PacketAddTelepadForPlayer_Client(int x, int y, int z, String name) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.name = name;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		name = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		ByteBufUtils.writeUTF8String(buf, name);
	}

	public static class HandlerPacketAddTelepadForPlayer_Client implements
			IMessageHandler<PacketAddTelepadForPlayer_Client, IMessage> {

		@Override
		public IMessage onMessage(PacketAddTelepadForPlayer_Client message,
				MessageContext ctx) {

			if (Telepads.proxy.getClientPlayer() == null)
				return null;

			TETelepad pad = (TETelepad) Telepads.proxy.getClientPlayer().worldObj
					.getTileEntity(message.x, message.y, message.z);

			pad.telepadname = message.name;
			pad.ownerName = Telepads.proxy.getClientPlayer().getGameProfile()
					.getName();
			pad.dimension = Telepads.proxy.getClientPlayer().worldObj.provider.dimensionId;

			int[] a = new int[] { message.x, message.y, message.z };

			PlayerPadData.get(Telepads.proxy.getClientPlayer()).getAllCoords()
					.add(a);
			PlayerPadData.get(Telepads.proxy.getClientPlayer()).getAllNames()
					.add(message.name);
			PlayerPadData.get(Telepads.proxy.getClientPlayer()).getAllDims()
					.add(pad.dimension);

			Telepads.proxy.getClientPlayer().worldObj.markBlockForUpdate(
					pad.xCoord, pad.yCoord, pad.zCoord);

			return null;
		}
	}

}
