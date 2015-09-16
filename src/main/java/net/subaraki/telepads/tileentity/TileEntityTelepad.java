package net.subaraki.telepads.tileentity;

import java.awt.Color;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.subaraki.telepads.Telepads;
import net.subaraki.telepads.inventory.ContainerTelePad;
import net.subaraki.telepads.util.Constants;

public class TileEntityTelepad extends TileEntity {
    
    private String telepadname = "TelePad";
    private int dimension;
    private String padOwner = "";
    private int colorFrame = new Color(26, 246, 172).getRGB();
    private int colorBase = new Color(243, 89, 233).getRGB();
    
    private boolean hasDimensionUpgrade = false;
    
    private static int MAX_TIME = 3 * 20;
    public int counter = MAX_TIME;
    private boolean guiOpen;
    public boolean isStandingOnPlatform = false;
    
   
    @Override
    public boolean canUpdate () {
        
        return true;
    }
    
    @Override
    public Packet getDescriptionPacket () {
        
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbt);
    }
    
    @Override
    public void onDataPacket (NetworkManager net, S35PacketUpdateTileEntity pkt) {
        
        this.readFromNBT(pkt.func_148857_g());
    }
    
    @Override
    public void readFromNBT (NBTTagCompound tag) {
        
        telepadname = (tag.getString("name"));
        padOwner = tag.getString("owner");
        dimension = tag.getInteger("dimension");
        hasDimensionUpgrade = tag.getBoolean("upgrade_dimension");
        this.colorBase = tag.getInteger("colorBase");
        this.colorFrame = tag.getInteger("colorFrame");
        
        super.readFromNBT(tag);
    }
    
    @Override
    public void writeToNBT (NBTTagCompound tag) {
        
        tag.setString("name", telepadname);
        tag.setString("owner", padOwner);
        tag.setInteger("dimension", dimension);
        tag.setBoolean("upgrade_dimension", hasDimensionUpgrade);
        tag.setInteger("colorBase", this.colorBase);
        tag.setInteger("colorFrame", this.colorFrame);
        
        super.writeToNBT(tag);
    }
    
    @Override
    public void updateEntity () {
        
        Telepads.proxy.createTelepadParticleEffect(xCoord, yCoord, zCoord, isStandingOnPlatform);
        
        if (!worldObj.isRemote) {
            
            AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
            List<EntityPlayer> playersInRange = worldObj.getEntitiesWithinAABB(EntityPlayer.class, aabb);
            
            if (isStandingOnPlatform) {
                
                if (playersInRange.isEmpty()) {
                    
                    changePlatformState(false);
                    return;
                }
                
                if (counter >= 0)
                    counter--;
                    
                activateTelepadGui(playersInRange);
                
            }
            
            else if (!playersInRange.isEmpty() && !isStandingOnPlatform)
                changePlatformState(true);
        }
    }
    
    /**
     * Resets the count down of the pad, sets that there is no player on the pad, and no player
     * using the gui. And causes a block update.
     */
    public void resetTE () {
        
        counter = MAX_TIME;
        isStandingOnPlatform = false;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        guiOpen = false;
    }
    
    /**
     * Activates the Telepad GUI. This will trigger the gui for the first player detected on
     * the pad, other players will be locked out, until that player leaves the pad.
     * 
     * @param playerInRange: A list of all players atempting to travel through the telepad.
     */
    private void activateTelepadGui (List<EntityPlayer> playerInRange) {
        
        for (EntityPlayer player : playerInRange) {
            
            if ((counter < 0) && !guiOpen && !(player.openContainer instanceof ContainerTelePad)) {
                
                guiOpen = true;
                player.openGui(Telepads.instance, Constants.GUI_ID_TELEPORT, worldObj, xCoord, yCoord, zCoord);
                markDirty();
                break;
            }
        }
    }
    
    /**
     * Updates the standing state of the platform. This is used to set whether or not a player
     * is currently standing on the pad. If the state is set to false, this will reset the
     * Telepad timer and cause a block update.
     * 
     * @param state: The new state for the pad. false means nobody is standing on it, true
     *            means that there is.
     */
    public void changePlatformState (boolean state) {
        
        isStandingOnPlatform = state;
        
        if (!state)
            resetTE();
    }
    
    
    public String getTelePadName(){
    	return telepadname;
    }
    
    public void setTelePadName(String name){
    	telepadname = name;
    }
    
    public int getDimension(){
    	return dimension;
    }
    
    public void setDimension(int dimensionID){
    	dimension = dimensionID;
    }
    
    public String getOwnerName(){
    	return padOwner;
    }

    public void setOwnerName(String name){
    	padOwner = name;
    }
    
    public void setFrameColor(int rgb){
    	colorFrame = rgb;
    }
    
    public void setBaseColor(int rgb){
    	colorBase = rgb;
    }
    
    public int getColorFrame(){
    	return colorFrame;
    }
    
    public int getColorBase(){
    	return colorBase;
    }
    
    public boolean hasDimensionUpgrade(){
    	return hasDimensionUpgrade;
    }
    
    public void addDimensionUpgrade(){
    	hasDimensionUpgrade = true;
    }
}
