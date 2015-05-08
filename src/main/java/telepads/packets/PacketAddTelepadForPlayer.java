package telepads.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import telepads.Telepads;
import telepads.block.TETelepad;
import telepads.util.PlayerPadData;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketAddTelepadForPlayer implements IMessage {

	public PacketAddTelepadForPlayer() {
	}
	
	int x;
	int y;
	int z;
	String name;
	
	public PacketAddTelepadForPlayer(int x, int y, int z, String name) {
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
	
	public static class HandlerPacketAddTelepadForPlayer implements IMessageHandler<PacketAddTelepadForPlayer, IMessage>{

		@Override
		public IMessage onMessage(PacketAddTelepadForPlayer message, MessageContext ctx) {
			
			TETelepad pad = (TETelepad) ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
			EntityPlayer player = ctx.getServerHandler().playerEntity;
			
			pad.telepadname = message.name;
			pad.ownerName = player.getGameProfile().getName();
			pad.dimension = player.worldObj.provider.dimensionId;

			int[] a = new int[]{message.x, message.y, message.z};

			PlayerPadData.get(player).getAllCoords().add(a);
			PlayerPadData.get(player).getAllNames().add(message.name);
			PlayerPadData.get(player).getAllDims().add(pad.dimension);

			player.worldObj.markBlockForUpdate(pad.xCoord,pad.yCoord,pad.zCoord);
			
			Telepads.SNW.sendTo(new PacketAddTelepadForPlayer_Client(message.x, message.y, message.z, message.name), (EntityPlayerMP) player);
			return null;
		}
	}
}
