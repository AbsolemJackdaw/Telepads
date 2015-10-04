package net.subaraki.telepads.common.network;

import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.util.Position;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.subaraki.telepads.Telepads;
import net.subaraki.telepads.tileentity.TileEntityTelepad;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncPoweredBlock implements IMessage {

	Position pos;
	boolean flag;

	public PacketSyncPoweredBlock(boolean flag, Position pos) {

		this.pos = pos;
		this.flag = flag;
	}

	@Override
	public void fromBytes (ByteBuf buf) {
		flag = buf.readBoolean();
		pos = new Position(buf);
	}

	@Override
	public void toBytes (ByteBuf buf) {
		buf.writeBoolean(flag);
		pos.write(buf);
	}

	public PacketSyncPoweredBlock() {

	}

	public static class PacketSyncPoweredBlockHandler implements IMessageHandler<PacketSyncPoweredBlock, IMessage> {

		@Override
		public IMessage onMessage (PacketSyncPoweredBlock packet, MessageContext ctx) {

			World world = Telepads.proxy.getClientWorld();
			TileEntity te = world.getTileEntity(packet.pos.getX(), packet.pos.getY(), packet.pos.getZ());

			if(te instanceof TileEntityTelepad){
				((TileEntityTelepad)te).setPowered(packet.flag);
			}
			
			

			return null;
		}
	}
}