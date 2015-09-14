package net.subaraki.telepads.tileentity;

import java.util.List;

import net.darkhax.bookshelf.util.EnumVanillaColors;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.subaraki.telepads.Telepads;
import net.subaraki.telepads.inventory.ContainerTelePad;
import net.subaraki.telepads.util.Constants;

public class TileEntityTelepad extends TileEntity {
    
    public String telepadname = "TelePad";
    public int dimension;
    public String ownerName = "";
    public boolean isStandingOnPlatform = false;
    public int color = EnumVanillaColors.WHITE.colorObj.getRGB();
    
    public static final int def_count = 3 * 20;
    public int counter = def_count;
    
    /**
     * checks if the teleport gui is already open to prevent it from openeing every tick
     */
    private boolean guiOpen;
    
    private boolean isUpgraded = false;
    
    @Override
    public boolean canUpdate () {
        
        return true;
    }
    
    public void activateUpgrade () {
        
        isUpgraded = true;
    }
    
    public boolean isUpgraded () {
        
        return isUpgraded;
    }
    
    /** Sets isStandingOnPlatform, and reset's TE if false */
    public void changePlatformState (boolean b) {
        
        isStandingOnPlatform = b;
        if (!b)
            resetTE();
    }
    
    @Override
    public Packet getDescriptionPacket () {
        
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbt);
    }
    
    @Override
    public void onDataPacket (NetworkManager net, S35PacketUpdateTileEntity pkt) {
        
        this.readFromNBT(pkt.func_148857_g()); // packet.data
    }
    
    @Override
    public void readFromNBT (NBTTagCompound tag) {
        
        telepadname = (tag.getString("name"));
        ownerName = tag.getString("owner");
        dimension = tag.getInteger("dimension");
        isUpgraded = tag.getBoolean("upgrade");
        this.color = tag.getInteger("color");
        
        super.readFromNBT(tag);
    }
    
    public void resetTE () {
        
        counter = def_count;
        isStandingOnPlatform = false;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        guiOpen = false;
    }
    
    @Override
    public void updateEntity () {
        
        Telepads.proxy.createTelepadParticleEffect(xCoord, yCoord, zCoord, isStandingOnPlatform);
        
        if (!worldObj.isRemote) {
            AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
            
            List<EntityPlayer> playerInAabb = worldObj.getEntitiesWithinAABB(EntityPlayer.class, aabb);
            
            if (isStandingOnPlatform) {
                
                if (playerInAabb.isEmpty()) {
                    changePlatformState(false);
                    return;
                }
                
                if (counter >= 0)
                    counter--;
                    
                updatePlatformLogic(playerInAabb);
                
            }
            else {
                if (!playerInAabb.isEmpty() && !isStandingOnPlatform) {
                    changePlatformState(true);
                    
                }
            }
        }
    }
    
    @Override
    public void writeToNBT (NBTTagCompound tag) {
        
        tag.setString("name", telepadname);
        tag.setString("owner", ownerName);
        tag.setInteger("dimension", dimension);
        tag.setBoolean("upgrade", isUpgraded);
        tag.setInteger("color", this.color);
        
        super.writeToNBT(tag);
    }
    
    /** gets called server side only */
    private void updatePlatformLogic (List<EntityPlayer> playerInAabb) {
        
        // player should not be null, as list given is passed trough an empty
        // check first
        for (EntityPlayer p : playerInAabb) {
            
            if ((counter < 0) && !guiOpen && !(p.openContainer instanceof ContainerTelePad)) {
                markDirty();
                guiOpen = true;
                p.openGui(Telepads.instance, Constants.GUI_ID_TELEPORT, worldObj, xCoord, yCoord, zCoord);
            }
        }
    }
    
    /**
     * called by ClientPacket to synch boolean to the client for particle updates-use
     */
    public void setStandingOnPlatform (boolean b) {
        
        isStandingOnPlatform = b;
    }
}
