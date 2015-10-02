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
import net.subaraki.telepads.Telepads;
import net.subaraki.telepads.util.Constants;
import net.subaraki.telepads.util.TeleportUtility;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketTeleport implements IMessage {

	/**
	 * The position to send the player to.
	 */
	public Position newPos;
	
	/**
	 * The position the player comes from.
	 */
	public Position oldPos;

	/**
	 * The dimension to send the player to.
	 */
	public int dimension;

	/**
	 * A packet to teleport the player to a given position from the client side. This packet
	 * must be sent from a client thread.
	 * 
	 * @param newPos: The position to send the player to.
	 * @param dimension: The dimension to send the player to.
	 * @param oldPos: The position the player comes from.
	 */
	public PacketTeleport(Position newPos, int dimension, Position oldPos) {

		this.oldPos = oldPos;
		this.dimension = dimension;
		this.newPos = newPos;
	}

	@Override
	public void fromBytes (ByteBuf buf) {

		newPos = new Position(buf);
		oldPos = new Position(buf);
		dimension = buf.readInt();
	}

	@Override
	public void toBytes (ByteBuf buf) {

		newPos.write(buf);
		oldPos.write(buf);
		buf.writeInt(dimension);
	}

	public PacketTeleport() {

	}

	public static class PacketTeleportHandler implements IMessageHandler<PacketTeleport, IMessage> {

		@Override
		public IMessage onMessage (PacketTeleport packet, MessageContext ctx) {

			EntityPlayer player = ctx.getServerHandler().playerEntity;

			if (packet.dimension == player.dimension){
				if(player.worldObj.getTileEntity(packet.newPos.getX(), packet.newPos.getY(), packet.newPos.getZ()) != null){
					if(packet.dimension == player.worldObj.provider.dimensionId)
						packet.newPos.sendEntityToPosition(player);

				}else
					removePad(player, packet.oldPos);
			}else{
				WorldServer worldToCheck = DimensionManager.getWorld(packet.dimension);
				if(worldToCheck!= null)
					if(worldToCheck.getTileEntity(packet.newPos.getX(), packet.newPos.getY(), packet.newPos.getZ()) != null){
						if(player instanceof EntityPlayerMP)
							TeleportUtility.transferPlayerToDimension((EntityPlayerMP) player, packet.dimension, packet.newPos);
					}else
						removePad(player, packet.oldPos);
			}
			return null;
		}
	}
	
	private static void removePad(EntityPlayer player, Position pos){
		
	}
}
