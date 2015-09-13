package net.subaraki.telepads.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.subaraki.telepads.Telepads;
import net.subaraki.telepads.util.Constants;

public class TelePadTileEntity extends TileEntity {
    
    // TE is "server side only" reflection
    // timer doesnt get updated client side, and can therefor not influence
    // particles directly
    
    public String telepadname = "TelePad";
    
    public int dimension;
    
    public String ownerName = "";
    
    public boolean isStandingOnPlatform = false;
    
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
    
    public void activateUpgrade(){
    	isUpgraded = true;
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
    public void readFromNBT (NBTTagCompound par1nbtTagCompound) {
        
        telepadname = (par1nbtTagCompound.getString("name"));
        ownerName = par1nbtTagCompound.getString("owner");
        dimension = par1nbtTagCompound.getInteger("dimension");
        isUpgraded = par1nbtTagCompound.getBoolean("upgrade");
        
        super.readFromNBT(par1nbtTagCompound);
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
        
        if (isStandingOnPlatform)
            playParticles(worldObj.rand, xCoord, yCoord, zCoord);
            
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
    public void writeToNBT (NBTTagCompound par1nbtTagCompound) {
        
        par1nbtTagCompound.setString("name", telepadname);
        par1nbtTagCompound.setString("owner", ownerName);
        par1nbtTagCompound.setInteger("dimension", dimension);
        par1nbtTagCompound.setBoolean("upgrade", isUpgraded);
        
        super.writeToNBT(par1nbtTagCompound);
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
    
    private void playParticles (Random rand, int x, int y, int z) {
        
        TelePadTileEntity te = (TelePadTileEntity) worldObj.getTileEntity(x, y, z);
        
        if (te == null)
            return;
            
        if (te.isStandingOnPlatform) {
            for (int l = 0; l < 100; ++l) {
                double d1 = y + (rand.nextFloat() * 1.5f);
                double d0 = x + rand.nextFloat();
                double d2 = 0.0D;
                double d3 = 0.0D;
                double d4 = 0.0D;
                int i1 = (rand.nextInt(2) * 2) - 1;
                int j1 = (rand.nextInt(2) * 2) - 1;
                d2 = (rand.nextFloat() - 0.5D) * 0.125D;
                d3 = (rand.nextFloat() - 0.5D) * 0.125D;
                d4 = (rand.nextFloat() - 0.5D) * 0.125D;
                d4 = rand.nextFloat() * 1.0F * j1;
                d2 = rand.nextFloat() * 1.0F * i1;
                this.worldObj.spawnParticle("portal", x + 0.5, d1, z + 0.5, d2, d3, d4);
            }
        }
        else {
            for (int l = 0; l < 5; ++l) {
                double d1 = y + (rand.nextFloat() * 1.5f);
                double d0 = z + rand.nextFloat();
                double d2 = 0.0D;
                double d3 = 0.0D;
                double d4 = 0.0D;
                int i1 = (rand.nextInt(2) * 2) - 1;
                int j1 = (rand.nextInt(2) * 2) - 1;
                d2 = (rand.nextFloat() - 0.5D) * 0.125D;
                d3 = (rand.nextFloat() - 0.5D) * 0.125D;
                d4 = (rand.nextFloat() - 0.5D) * 0.125D;
                d4 = rand.nextFloat() * 1.0F * j1;
                d2 = rand.nextFloat() * 1.0F * i1;
                worldObj.spawnParticle("portal", x + 0.5, d1, z + 0.5, d2, d3, d4);
            }
        }
    }
}
