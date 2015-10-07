package net.subaraki.telepads.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.util.Position;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.subaraki.telepads.client.gui.GuiRemovePad;
import net.subaraki.telepads.handler.PlayerLocations.TelepadEntry;
import net.subaraki.telepads.tileentity.TileEntityTelepad;
import net.subaraki.telepads.util.TeleportUtility;

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
	 * flag to force teleport and bypass checking if a tile entity exists to
	 * teleport too
	 * */
	public boolean force;

	/**
	 * A packet to teleport the player to a given position from the client side.
	 * This packet must be sent from a client thread.
	 * 
	 * @param newPos
	 *            : The position to send the player to.
	 * @param dimension
	 *            : The dimension to send the player to.
	 * @param oldPos
	 *            : The position the player comes from.
	 * @param forceTeleport
	 *            : Flag to force teleport and bypass checking if a tile entity
	 *            exists to teleport too
	 */
	public PacketTeleport(Position newPos, int dimension, Position oldPos,
			boolean forceTeleport) {

		this.oldPos = oldPos;
		this.dimension = dimension;
		this.newPos = newPos;
		this.force = forceTeleport;
	}

	@Override
	public void fromBytes(ByteBuf buf) {

		newPos = new Position(buf);
		oldPos = new Position(buf);
		dimension = buf.readInt();
		force = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {

		newPos.write(buf);
		oldPos.write(buf);
		buf.writeInt(dimension);
		buf.writeBoolean(force);
	}

	public PacketTeleport() {

	}

	public static class PacketTeleportHandler implements
			IMessageHandler<PacketTeleport, IMessage> {

		@Override
		public IMessage onMessage(PacketTeleport packet, MessageContext ctx) {

			EntityPlayer player = ctx.getServerHandler().playerEntity;

			if (packet.dimension == player.dimension) {
				if (packet.force) {
					packet.newPos.sendEntityToPosition(player);
					return null;
				}
				TileEntity te = player.worldObj.getTileEntity(
						packet.newPos.getX(), packet.newPos.getY(),
						packet.newPos.getZ());
				if (te != null && te instanceof TileEntityTelepad) {
					TileEntityTelepad telepad = (TileEntityTelepad) player.worldObj
							.getTileEntity(packet.newPos.getX(),
									packet.newPos.getY(), packet.newPos.getZ());
					if (!telepad.isPowered()) {
						if (packet.dimension == player.worldObj.provider.dimensionId)
							packet.newPos.sendEntityToPosition(player);
					} else
						player.addChatMessage(new ChatComponentText(
								"This pad was powered off"));

				} else
					removePad(player, packet.newPos, packet.oldPos,
							packet.dimension);
			} else {
				WorldServer worldToCheck = DimensionManager
						.getWorld(packet.dimension);
				if (worldToCheck != null) {
					TileEntity te = worldToCheck.getTileEntity(
							packet.newPos.getX(), packet.newPos.getY(),
							packet.newPos.getZ());
					if (te != null && te instanceof TileEntityTelepad) {
						TileEntityTelepad telepad = (TileEntityTelepad) worldToCheck
								.getTileEntity(packet.newPos.getX(),
										packet.newPos.getY(),
										packet.newPos.getZ());

						if (packet.force) {
							if (!telepad.isPowered())
								TeleportUtility.transferPlayerToDimension(
										(EntityPlayerMP) player,
										packet.dimension, packet.newPos);
							return null;
						}

						if (!telepad.isPowered()) {
							if (player instanceof EntityPlayerMP)
								TeleportUtility.transferPlayerToDimension(
										(EntityPlayerMP) player,
										packet.dimension, packet.newPos);
						} else
							player.addChatMessage(new ChatComponentText(
									"This pad was powered off"));
					} else
						removePad(player, packet.newPos, packet.oldPos,
								packet.dimension);
				}
			}
			return null;
		}
	}

	private static void removePad(EntityPlayer player,
			Position removedDestinyPos, Position oldExisitingPostion,
			int dimension) {

		// TODO fix direct cast
		Minecraft.getMinecraft().displayGuiScreen(
				new GuiRemovePad(player, (TileEntityTelepad) player.worldObj
						.getTileEntity(oldExisitingPostion.getX(),
								oldExisitingPostion.getY(),
								oldExisitingPostion.getZ()))
						.setEntryToRemove(new TelepadEntry("stubName",
								dimension, removedDestinyPos, false, false)));

	}
}
