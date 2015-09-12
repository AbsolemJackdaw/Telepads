package net.subaraki.telepads.handler;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.darkhax.bookshelf.util.Position;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class PlayerLocationProperties implements IExtendedEntityProperties {
    
    public static final String PROP_NAME = "TeleporterPositions";
    
    private EntityPlayer player;
    private List<Position> positions;
    
    public PlayerLocationProperties(EntityPlayer player) {
        
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
            positions.add(new Position(positionList.getCompoundTagAt(0)));
            
        this.positions = positions;
    }
    
    @Override
    public void init (Entity entity, World world) {
    
    }
    
    /**
     * Retrieves an instance of PlayerLocationProperties from an instance of EnityPlayer.
     * 
     * @param player: An instance of the player to retrieve the properties from.
     * @return PlayerLocationProperties: The found instance of PlayerLocationProperties. This
     *         will be null if none is found.
     */
    public static PlayerLocationProperties getProperties (EntityPlayer player) {
        
        return (PlayerLocationProperties) player.getExtendedProperties(PROP_NAME);
    }
    
    /**
     * Sets a player up with a new instance of PlayerLocationProperties.
     * 
     * @param player: An instance of the player to set properties to.
     * @return PlayerLocationProperties: An instance of the PlayerLocationProperties set to the
     *         player.
     */
    public static PlayerLocationProperties setProperties (EntityPlayer player) {
        
        player.registerExtendedProperties(PROP_NAME, new PlayerLocationProperties(player));
        return getProperties(player);
    }
    
    /**
     * Checks to see if a player has a valid PlayerLocationProperties instance.
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
     * PlayerLocationProperties.
     * 
     * @param properties: The instance of PlayerLocationProperties to pull everything from.
     */
    public void copy (PlayerLocationProperties properties) {
        
        this.positions = properties.positions;
    }
    
    /**
     * Provides a list of all Positions linked to this player.
     * 
     * @return List<Position>: A list containing all positions linked to the player.
     */
    public List<Position> getPositions () {
        
        return this.positions;
    }
}