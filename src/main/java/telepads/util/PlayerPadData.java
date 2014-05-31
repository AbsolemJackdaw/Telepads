package telepads.util;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import telepads.TelepadProxyServer;
import telepads.block.TETelepad;
import cpw.mods.fml.common.FMLLog;

public class PlayerPadData implements IExtendedEntityProperties{

	public final static String EXT_PROP_NAME = "PlayerPadData";
	public static final PlayerPadData get(EntityPlayer p) {
		return (PlayerPadData) p.getExtendedProperties(EXT_PROP_NAME);
	}

	private static String getSaveKey(EntityPlayer player) {
		return player.getDisplayName() + ":" + EXT_PROP_NAME;
	}
	/**
	 * This cleans up the onEntityJoinWorld event by replacing most of the code
	 * with a single line: ExtendedPlayer.loadProxyData((EntityPlayer) event.entity));
	 */
	public static void loadProxyData(EntityPlayer player) {
		PlayerPadData playerData = PlayerPadData.get(player);
		NBTTagCompound savedData = TelepadProxyServer.getEntityData(getSaveKey(player));

		if(savedData != null) {
			playerData.loadNBTData(savedData);
		}
	}
	public static final void register(EntityPlayer player) {
		if (player != null) {
			player.registerExtendedProperties(EXT_PROP_NAME, new PlayerPadData(player));
			FMLLog.getLogger().info("Player properties registered for Telepads");
		}
	}

	public static void saveProxyData(EntityPlayer player) {
		PlayerPadData playerData = PlayerPadData.get(player);
		NBTTagCompound savedData = new NBTTagCompound();

		playerData.saveNBTData(savedData);
		TelepadProxyServer.storeEntityData(getSaveKey(player), savedData);
	}

	public EntityPlayer player;

	private ArrayList<int[]> allCoords = new ArrayList<int[]>();

	private ArrayList<Integer> allDims = new ArrayList<Integer>();

	private ArrayList<String> allNames = new ArrayList<String>();

	public PlayerPadData(EntityPlayer p) {
		if (p != null) {
			player = p;
		}
	}

	public ArrayList<int[]> getAllCoords() {
		return allCoords;
	}

	public ArrayList<Integer> getAllDims() {
		return allDims;
	}

	public ArrayList<String> getAllNames() {
		return allNames;
	}

	public EntityPlayer getPlayer() {
		return player;
	}

	@Override
	public void init(Entity entity, World world) {

	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		//reset arrays to prevent doubles and tripples
		allCoords = new ArrayList<int[]>();
		allNames = new ArrayList<String>();
		allDims = new ArrayList<Integer>();

		int i = compound.getInteger("Size");

		for(int a = 0; a < i ; a++){
			allCoords.add(compound.getIntArray("allCoords_"+a));
			allDims.add(compound.getInteger("allDims_"+a));
			allNames.add(compound.getString("allNames_"+a));
		}
		trim();
	}


	public void removePad(TETelepad pad){

		int a = pad.xCoord;
		int b = pad.yCoord;
		int c = pad.zCoord;

		for(int i = 0; i < allCoords.size(); i++) {
			if(allCoords.get(i)[0] == a) {
				if(allCoords.get(i)[1] == b) {
					if(allCoords.get(i)[2] == c){
						allCoords.remove(i);
						allNames.remove(i);
						allDims.remove(i);
					}
				}
			}
		}
		trim();
	}


	@Override
	public void saveNBTData(NBTTagCompound compound) {
		compound.setInteger("Size", allCoords.size());

		for(int i = 0; i < allCoords.size(); i++) {
			compound.setIntArray("allCoords_"+i, allCoords.get(i));
		}

		for(int i = 0; i < allDims.size(); i++) {
			compound.setInteger("allDims_"+i, allDims.get(i));
		}

		for(int i = 0; i < allNames.size(); i++) {
			compound.setString("allNames_"+i, allNames.get(i));
		}

		trim();

	}

	private void trim(){
		allCoords.trimToSize();
		allDims.trimToSize();
		allNames.trimToSize();
	}
}
