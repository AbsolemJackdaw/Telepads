package net.subaraki.telepads.handler;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.darkhax.bookshelf.util.Position;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.subaraki.telepads.Telepads;
import net.subaraki.telepads.common.network.PacketSyncTelepadEntries;
import cpw.mods.fml.common.network.ByteBufUtils;

public class PlayerLocations implements IExtendedEntityProperties {

	/**
	 * The name of the NBTTagCompound which holds all of the data handled by this class.
	 */
	public static final String PROP_NAME = "TelepadLocations";

	/**
	 * An instance of the specific player that is being read and written to.
	 */
	private EntityPlayer player;

	/**
	 * A list of entries that this player has acces to.
	 */
	private List<TelepadEntry> entries;

	/**
	 * Constructs a new PlayerLocations instance. This should only be used when creating new
	 * properties for a player. To add properties to a player, look at setProperties.
	 * 
	 * @param player: The player that the new PlayerLocations instance is for.
	 */
	public PlayerLocations(EntityPlayer player) {

		this.player = player;
		this.entries = new ArrayList<TelepadEntry>();
	}

	@Override
	public void saveNBTData (NBTTagCompound compound) {

		Telepads.printDebugMessage("Saving Telepad data");
		NBTTagList entryList = new NBTTagList();

		for (TelepadEntry entry : this.entries)
			entryList.appendTag(entry.writeToNBT(new NBTTagCompound()));

		compound.setTag(PROP_NAME, entryList);
	}

	@Override
	public void loadNBTData (NBTTagCompound compound) {

		Telepads.printDebugMessage("Loading Telepad data");
		List<TelepadEntry> entryList = new ArrayList<TelepadEntry>();
		NBTTagList entryTagList = compound.getTagList(PROP_NAME, 10);

		for (int tagPos = 0; tagPos < entryTagList.tagCount(); tagPos++)
			entryList.add(new TelepadEntry(entryTagList.getCompoundTagAt(tagPos)));

		this.entries = entryList;
	}

	@Override
	public void init (Entity entity, World world) {

	}

	/**
	 * Retrieves an instance of PlayerLocations from an instance of EnityPlayer.
	 * 
	 * @param player: An instance of the player to retrieve the properties from.
	 * @return PlayerLocations: The found instance of PlayerLocations. This will be null if
	 *         none is found.
	 */
	public static PlayerLocations getProperties (EntityPlayer player) {

		return (PlayerLocations) player.getExtendedProperties(PROP_NAME);
	}

	/**
	 * Sets a player up with a new instance of PlayerLocations.
	 * 
	 * @param player: An instance of the player to set properties to.
	 * @return PlayerLocations: An instance of the PlayerLocations set to the player.
	 */
	public static PlayerLocations setProperties (EntityPlayer player) {

		player.registerExtendedProperties(PROP_NAME, new PlayerLocations(player));
		return getProperties(player);
	}

	/**
	 * Checks to see if a player has a valid PlayerLocations instance.
	 * 
	 * @param player: The instance of the player to check.
	 * @return boolean: If the player has a valid instance true will be returned, otherwise
	 *         false.
	 */
	public static boolean hasProperties (EntityPlayer player) {

		return getProperties(player) != null;
	}

	/**
	 * Sets all of the stored properties, to reflect those of another instance of
	 * PlayerLocations.
	 * 
	 * @param properties: The instance of PlayerLocations to pull everything from.
	 */
	public void copy (PlayerLocations properties) {

		this.entries = properties.entries;
	}

	/**
	 * Provides a list of all TelepadEntrys that the player has access to.
	 * 
	 * @return List<TelepadEntry>: A list of every TelepadEntry stored on the players file.
	 */
	public List<TelepadEntry> getEntries () {

		if (this.entries == null)
			this.entries = new ArrayList<TelepadEntry>();

		return this.entries;
	}

	/**
	 * Sets the list of all TelepadEntrys to a new list of entries. This will delete all
	 * previous entries. This will also sync all server side data to the client.
	 * 
	 * @param entries: The new list of entries that will replace the old ones.
	 */
	public void overrideEntries (List<TelepadEntry> entries) {

		this.entries = entries;
		this.sync();
	}

	/**
	 * Adds a new TelepadEntry to the players list of entries. This will also sync all server
	 * side data to the client.
	 * 
	 * @param entry: The new TelepadEntry to add for the player.
	 */
	public void addEntry (TelepadEntry entry) {

		this.getEntries().add(entry);
		this.sync();
	}

	/**
	 * Removes a specified TelepadEntry from the players list of entries. This will also sync
	 * all server side data to the client.
	 * 
	 * @param entry: The entry that you want to remove.
	 */
	public void removeEntry (TelepadEntry entry) {

		for(TelepadEntry tpe : getEntries())
			if(tpe.position.equals(entry.position))
				if(tpe.dimensionID == entry.dimensionID){
					//					if(tpe.entryName.equals(entry.entryName)){
					this.getEntries().remove(tpe);
					break;
				}
		this.sync();
	}

	/**
	 * Synchronizes the data between the server and the client. In some cases the client's
	 * version of the PlayerLocation may be out of sync. Calling this will fix that.
	 */
	public void sync () {

		if (player instanceof EntityPlayerMP)
			Telepads.instance.network.sendTo(new PacketSyncTelepadEntries(player.getUniqueID(), this.entries), (EntityPlayerMP) player);
	}

	/**
	 * Provides a suggested TelepadEntry name based on the players dimension, and existing
	 * entries.
	 * 
	 * @return String: A name that can be used for a TelepadEntry. Example: Surface 9
	 */
	public String getSuggestedEntryName () {

		String entryName = "Unknown Dimension";
		int entryIndex = 1;
		WorldProvider provider = DimensionManager.getProvider(player.dimension);

		if (provider != null)
			entryName = provider.getDimensionName();

		for (int index = 0; index < this.getEntries().size(); index++)
			if (this.getEntries().get(index).dimensionID == player.dimension)
				entryIndex++;

		return entryName + " " + entryIndex;
	}

	public static class TelepadEntry {

		/**
		 * The user defined name for the TelePad entry.
		 */
		public String entryName;

		/**
		 * The dimension that the TelePad entry is located in.
		 */
		public int dimensionID;

		/**
		 * The coordinates of the TelePad entry.
		 */
		public Position position;

		/***/
		public boolean isPowered;
		public boolean hasTransmitter;
		
		/**
		 * Creates a TelepadEntry from a ByteBuf. This is useful for reading from networking.
		 * 
		 * @param buf: A ByteBuf containing the data needed to create a TelepadEntry.
		 */
		public TelepadEntry(ByteBuf buf) {

			this(ByteBufUtils.readUTF8String(buf), buf.readInt(), new Position(buf), buf.readBoolean(), buf.readBoolean());
		}

		/**
		 * Creates a TelepadEntry from a NBTTagCompound. This is used for reading from
		 * NBTTagCompound.
		 * 
		 * @param tag: An NBTTagCompound to read the required data from.
		 */
		public TelepadEntry(NBTTagCompound tag) {

			this(tag.getString("entryName"), tag.getInteger("dimensionID"), new Position(tag), tag.getBoolean("power"), tag.getBoolean("transmitter"));
		}

		/**
		 * Creates a new TelepadEntry. This is used to represent an entry that a player can
		 * teleport to.
		 * 
		 * @param name: A display name to use for the entry.
		 * @param dimension: The id of the dimension that this entry is within.
		 * @param pos: The Position of this TelepadEntry.
		 * @param isPowered defaults to false. wether this entry's tile entity is redstone powered or not
		 * @param hasTransmitter defaults to false. wether this entry's tile entity has a transmitter upgrade
		 */
		public TelepadEntry(String name, int dimension, Position pos, boolean isPowered, boolean hasTransmitter) {

			this.entryName = name;
			this.dimensionID = dimension;
			this.position = pos;
			this.isPowered = isPowered;
			this.hasTransmitter = hasTransmitter;
		}

		/**
		 * Writes the TelepadEntry to a NBTTagCompound.
		 * 
		 * @param tag: The tag to write the TelepadEntry to.
		 * @return NBTTagCompound: An NBTTagCompound containing all of the TelepadEntry data.
		 */
		public NBTTagCompound writeToNBT (NBTTagCompound tag) {

			tag.setString("entryName", this.entryName);
			tag.setInteger("dimensionID", this.dimensionID);
			this.position.write(tag);
			tag.setBoolean("power",	isPowered);
			tag.setBoolean("transmitter", hasTransmitter);
			return tag;
		}

		/**
		 * Write the TelepadEntry to a ByteBuf.
		 * 
		 * @param buf: The ByteBuf to write the TelepadEntry to.
		 */
		public void writeToByteBuf (ByteBuf buf) {

			ByteBufUtils.writeUTF8String(buf, this.entryName);
			buf.writeInt(this.dimensionID);
			this.position.write(buf);
			buf.writeBoolean(isPowered);
			buf.writeBoolean(hasTransmitter);
		}

		@Override
		public String toString () {

			return "Entry Name: " + this.entryName + " DimensionID: " + this.dimensionID + " " + this.position.toString();
		}

		@Override
		public Object clone () {

			return new TelepadEntry(this.entryName, this.dimensionID, this.position, this.isPowered, this.hasTransmitter);
		}

		@Override
		public boolean equals (Object compared) {

			if (!(compared instanceof TelepadEntry))
				return false;

			TelepadEntry entry = (TelepadEntry) compared;
			return this.entryName.equals(entry.entryName) && this.dimensionID == entry.dimensionID && this.position.equals(entry.position);
		}
		
		public void setPowered(boolean flag){
			isPowered = flag;
		}
		
		public void setTransmitter(boolean flag){
			hasTransmitter = flag;
		}
	}
}