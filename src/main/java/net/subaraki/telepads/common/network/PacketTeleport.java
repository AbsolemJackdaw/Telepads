package net.subaraki.telepads.common.network;

import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.util.Position;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.subaraki.telepads.util.TeleportUtility;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

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

			//			if (packet.dimension != player.dimension)
			//									player.travelToDimension(packet.dimension);
			//
			//					packet.pos.sendEntityToPosition(player);

			if(player.worldObj.getTileEntity(packet.pos.getX(), packet.pos.getY(), packet.pos.getZ()) != null){
				if(packet.dimension == player.worldObj.provider.dimensionId)
					packet.pos.sendEntityToPosition(player);

			}else{
				WorldServer worldToCheck = DimensionManager.getWorld(packet.dimension);

				if(worldToCheck.getTileEntity(packet.pos.getX(), packet.pos.getY(), packet.pos.getZ()) != null){
					if(player instanceof EntityPlayerMP)
						TeleportUtility.transferPlayerToDimension((EntityPlayerMP) player, packet.dimension, packet.pos);
				}
			}
			return null;
		}
	}
}
