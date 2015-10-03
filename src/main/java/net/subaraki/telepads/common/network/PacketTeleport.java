package net.subaraki.telepads.common.network;

import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.util.Position;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.subaraki.telepads.tileentity.TileEntityTelepad;
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
				TileEntity te = player.worldObj.getTileEntity(packet.newPos.getX(), packet.newPos.getY(), packet.newPos.getZ());
				if( te != null && te instanceof TileEntityTelepad){
					TileEntityTelepad telepad = (TileEntityTelepad) player.worldObj.getTileEntity(packet.newPos.getX(), packet.newPos.getY(), packet.newPos.getZ());
					if(!telepad.isPowered()){
						if(packet.dimension == player.worldObj.provider.dimensionId)
							packet.newPos.sendEntityToPosition(player);
					}else
						player.addChatMessage(new ChatComponentText("This pad was powered off"));

				}else
					removePad(player, packet.oldPos);
			}else{
				WorldServer worldToCheck = DimensionManager.getWorld(packet.dimension);
				if(worldToCheck!= null){
					TileEntity te = player.worldObj.getTileEntity(packet.newPos.getX(), packet.newPos.getY(), packet.newPos.getZ());
					if(te != null && te instanceof TileEntityTelepad){
						TileEntityTelepad telepad = (TileEntityTelepad) player.worldObj.getTileEntity(packet.newPos.getX(), packet.newPos.getY(), packet.newPos.getZ());
						if(!telepad.isPowered()){					
							if(player instanceof EntityPlayerMP)
								TeleportUtility.transferPlayerToDimension((EntityPlayerMP) player, packet.dimension, packet.newPos);
						}else
							player.addChatMessage(new ChatComponentText("This pad was powered off"));
					}else
						removePad(player, packet.oldPos);
				}
			}
			return null;
		}
	}

	private static void removePad(EntityPlayer player, Position pos){

	}
}
