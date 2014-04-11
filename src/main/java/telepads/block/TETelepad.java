package telepads.block;

import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import telepads.Telepads;
import telepads.util.TelePadGuiHandler;

public class TETelepad extends TileEntity{

	public String telepadname = "TelePad";

	public int dimension;

	public String ownerName = "";

	public boolean isStandingOnPlatform = false;

	public static final int def_count = 5*20;
	public int counter = def_count;

	/**Set when player walks on a pad*/
	public EntityPlayer playerStandingOnPad = null;

	private int previousSize = 0;
	private boolean guiOpen;

	public boolean isUniversal;

	public boolean lockedUniversal;

	public void addRegister(){

		EntityPlayer p = worldObj.getPlayerEntityByName(ownerName);
		//if player hasnt got one
		if(!p.inventory.hasItem(Telepads.register)){

			ItemStack stack = new ItemStack(Telepads.register);

			EntityItem ei = new EntityItem(worldObj, xCoord, yCoord, zCoord, stack);
			if(!worldObj.isRemote)
				worldObj.spawnEntityInWorld(ei);

		}
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	/**Sets isStandingOnPlatform, and reset's TE if false*/
	public void changePlatformState(boolean b){
		isStandingOnPlatform = b;
		if(!b){
			resetTE();
			playerStandingOnPad = null;
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		System.out.println("getDescriptionPacket");
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.func_148857_g());  //packet.data
		System.out.println("onDataPacket");

	}

	@Override
	public void readFromNBT(NBTTagCompound par1nbtTagCompound) {

		telepadname = (par1nbtTagCompound.getString("name"));
		ownerName = par1nbtTagCompound.getString("owner");
		dimension = par1nbtTagCompound.getInteger("dimension");
		isUniversal = par1nbtTagCompound.getBoolean("isUniversal");
		lockedUniversal = par1nbtTagCompound.getBoolean("lockedUniversal");

		super.readFromNBT(par1nbtTagCompound);
		System.out.println("read");
	}

	/**Resets the TelePad and notifies the player of it : aka, send chat mesage*/
	public void ResetAndNotify(String message, EntityPlayer p){
		if(!worldObj.isRemote)
			p.addChatComponentMessage(new ChatComponentText(message));
		resetTE();
	}

	public void resetTE(){
		counter = def_count;
		isStandingOnPlatform = false;
		markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord,zCoord);
		guiOpen = false;
	}


	@Override
	public void updateEntity() {


		AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord+0.5, yCoord+0.5, zCoord+0.5);

		List<EntityPlayer> playerInAabb = worldObj.getEntitiesWithinAABB(EntityPlayer.class, aabb);

		if(isStandingOnPlatform)
			if(playerInAabb.isEmpty() || !playerInAabb.contains(playerStandingOnPad))
				changePlatformState(false);

		for(EntityPlayer p : playerInAabb){

			if(p!=null){
				if(isStandingOnPlatform == false)//check to prevent packet from spamming
					changePlatformState(true);

				if(counter >=0)counter --;
			}

			if((counter < 0) && !guiOpen)
				if(p != null)
					if(!p.inventory.hasItem(Telepads.register)){
						p.addChatMessage(new ChatComponentText("I forgot my register !"));
						p.addChatMessage(new ChatComponentText("(You got a new register from the Telepad"));
						ItemStack is = new ItemStack(Telepads.register);
						if(p.inventory.addItemStackToInventory(is))
							p.inventory.addItemStackToInventory(is);
						else{
							EntityItem ei = new EntityItem(worldObj, xCoord, yCoord, zCoord, is);
							if(!worldObj.isRemote)
								worldObj.spawnEntityInWorld(ei);
						}
						resetTE();
					}else{
						markDirty();
						guiOpen = true;
						p.openGui(Telepads.instance, TelePadGuiHandler.TELEPORT, worldObj, xCoord, yCoord, zCoord);
					}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound par1nbtTagCompound) {

		par1nbtTagCompound.setString("name", telepadname);
		par1nbtTagCompound.setString("owner", ownerName);
		par1nbtTagCompound.setInteger("dimension", dimension);
		par1nbtTagCompound.setBoolean("isUniversal", isUniversal);
		par1nbtTagCompound.setBoolean("lockedUniversal", lockedUniversal);

		super.writeToNBT(par1nbtTagCompound);
		System.out.println("write");
	}
}
