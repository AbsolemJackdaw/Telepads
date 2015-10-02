package net.subaraki.telepads.common.network;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

import net.darkhax.bookshelf.util.Utilities;
import net.subaraki.telepads.handler.PlayerLocations;
import net.subaraki.telepads.handler.PlayerLocations.TelepadEntry;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketAddTelepadEntry implements IMessage {

	/**
	 * The UUID of the player to add the new TelepadEntry to.
	 */
	private UUID playerUUID;

	/**
	 * The entry to be added to the player's list of locations.
	 */
	private TelepadEntry entry;

	/**
	 * A packet to add a new TelepadEntry to a player's list of locations. This packet is used
	 * to send data from the client to the server, and should not be sent from a server thread.
	 * When this packet is handled on the server side, a sync packet with automatically be sent
	 * back to the client to ensure everything is consistent.
	 * 
	 * @param playerUUID: The UUID of the player to add the new TelepadEntry to.
	 * @param entry: The TelepadEntry to be added to the player's list of locations.
	 */
	public PacketAddTelepadEntry(UUID playerUUID, TelepadEntry entry) {

		this.playerUUID = playerUUID;
		this.entry = entry;
	}

	@Override
	public void fromBytes (ByteBuf buf) {

		this.playerUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
		this.entry = new TelepadEntry(buf);
	}

	@Override
	public void toBytes (ByteBuf buf) {

		ByteBufUtils.writeUTF8String(buf, this.playerUUID.toString());
		this.entry.writeToByteBuf(buf);
	}

	public PacketAddTelepadEntry() {

	}

	public static class PacketAddTelepadEntryHandler implements IMessageHandler<PacketAddTelepadEntry, IMessage> {

		@Override
		public IMessage onMessage (PacketAddTelepadEntry packet, MessageContext ctx) {

			PlayerLocations locations = PlayerLocations.getProperties(Utilities.getPlayerFromUUID(ctx.getServerHandler().playerEntity.worldObj, packet.playerUUID));

			for(TelepadEntry tpe : locations.getEntries())
				if(tpe.dimensionID != packet.entry.dimensionID)
					if(!tpe.position.equals(packet.entry.position)){
						locations.addEntry(packet.entry);
						locations.sync();
					}

			return null;
		}
	}
}