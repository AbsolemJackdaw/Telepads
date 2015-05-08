//package telepads.packets;
//
//import telepads.Telepads;
//import telepads.block.TETelepad;
//import io.netty.buffer.ByteBuf;
//import cpw.mods.fml.common.network.simpleimpl.IMessage;
//import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
//import cpw.mods.fml.common.network.simpleimpl.MessageContext;
//
//public class PacketSetOnPlatform_Server implements IMessage {
//
//	public PacketSetOnPlatform_Server() {
//	}
//
//	private int x;
//	private int y;
//	private int z;
//	private boolean bool;
//
//	public PacketSetOnPlatform_Server(int x, int y, int z, boolean bool) {
//		this.x = x;
//		this.y = y;
//		this.z = z;
//		this.bool = bool;
//	}
//
//	@Override
//	public void fromBytes(ByteBuf buf) {
//		x = buf.readInt();
//		y = buf.readInt();
//		z = buf.readInt();
//		bool = buf.readBoolean();
//	}
//
//	@Override
//	public void toBytes(ByteBuf buf) {
//		buf.writeInt(x);
//		buf.writeInt(y);
//		buf.writeInt(z);
//		buf.writeBoolean(bool);
//	}
//
//	public static class HandlerPacketSetOnPlatform_Server implements IMessageHandler<PacketSetOnPlatform_Server, IMessage>{
//
//		@Override
//		public IMessage onMessage(PacketSetOnPlatform_Server message, MessageContext ctx) {
//
//			TETelepad pad = (TETelepad) ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
//			if(pad == null)
//				return null;
//			
//			pad.setStandingOnPlatform(message.bool);
//			
//			Telepads.SNW.sendTo(new PacketSetOnPlatform_Client(pad, message.bool), ctx.getServerHandler().playerEntity);
//			return null;
//		}
//	}
//
//}
