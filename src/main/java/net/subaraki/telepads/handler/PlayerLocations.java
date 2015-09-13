package net.subaraki.telepads.handler;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.util.Position;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.subaraki.telepads.Telepads;
import net.subaraki.telepads.common.network.PacketSyncPositions;

public class PlayerLocations implements IExtendedEntityProperties {
    
    public static final String PROP_NAME = "TeleporterPositions";
    
    private EntityPlayer player;
    private List<Position> positions;
    
    public PlayerLocations(EntityPlayer player) {
        
        this.player = player;
        this.positions = new ArrayList<Position>();
    }
    
    @Override
    public void saveNBTData (NBTTagCompound compound) {
        
        NBTTagList positionList = new NBTTagList();
        
        for (Position pos : positions)
            positionList.appendTag(pos.write(new NBTTagCompound()));
            
        compound.setTag("positions", positionList);
    }
    
    @Override
    public void loadNBTData (NBTTagCompound compound) {
        
        List<Position> positions = new ArrayList<Position>();
        NBTTagList positionList = compound.getTagList("positions", 10);
        
        for (int tagPos = 0; tagPos < positionList.tagCount(); tagPos++)
            positions.add(new Position(positionList.getCompoundTagAt(tagPos)));
            
        this.positions = positions;
        sync();
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
        
        this.positions = properties.positions;
    }
    
    /**
     * Provides a list of all Positions linked to this player.
     * 
     * @return List<Position>: A list containing all positions linked to the player.
     */
    public List<Position> getPositions () {
        
        if (this.positions == null)
            this.positions = new ArrayList<Position>();
            
        return this.positions;
    }
    
    /**
     * Sets the list of positions linked to the player, to a new list.
     * 
     * @param positions: The list of new positions to se to the player data.
     */
    public void setPositions (List<Position> positions) {
        
        this.positions = positions;
    }
    
    /**
     * Synchronizes the players PlayerLocations from the server to the client. This will take
     * all of the data from the server side, and ensure that the client side data reflects it.
     * This method should only be called from a server side thread. Client sided calls are
     * automatically ignored.
     */
    public void sync () {
        
        if (FMLCommonHandler.instance().getSide().equals(Side.SERVER))
            Telepads.instance.network.sendTo(new PacketSyncPositions(player.getUniqueID(), this.positions), (EntityPlayerMP) player);
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
        
        /**
         * Creates a TelepadEntry from a ByteBuf. This is useful for reading from networking.
         * 
         * @param buf: A ByteBuf containing the data needed to create a TelepadEntry.
         */
        public TelepadEntry(ByteBuf buf) {
            
            this(ByteBufUtils.readUTF8String(buf), buf.readInt(), new Position(buf));
        }
        
        /**
         * Creates a TelepadEntry from a NBTTagCompound. This is used for reading from
         * NBTTagCompound.
         * 
         * @param tag: An NBTTagCompound to read the required data from.
         */
        public TelepadEntry(NBTTagCompound tag) {
            
            this(tag.getString("entryName"), tag.getInteger("dimensionID"), new Position(tag));
        }
        
        /**
         * Creates a new TelepadEntry. This is used to represent an entry that a player can
         * teleport to.
         * 
         * @param name: A display name to use for the entry.
         * @param dimension: The id of the dimension that this entry is within.
         * @param pos: The Position of this TelepadEntry.
         */
        public TelepadEntry(String name, int dimension, Position pos) {
            
            this.entryName = name;
            this.dimensionID = dimension;
            this.position = pos;
        }
        
        /**
         * Writes the TelepadEntry to a NBTTagCompound.
         * 
         * @param tag: The tag to write the TelepadEntry to.
         */
        public void writeToNBT (NBTTagCompound tag) {
            
            tag.setString("entryName", this.entryName);
            tag.setInteger("dimensionID", this.dimensionID);
            this.position.write(tag);
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
        }
    }
}