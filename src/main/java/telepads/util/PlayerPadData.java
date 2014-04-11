package telepads.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import telepads.SProxy;
import telepads.Serverpacket;
import telepads.Telepads;
import telepads.block.TETelepad;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class PlayerPadData implements IExtendedEntityProperties{

	public final static String EXT_PROP_NAME = "PlayerPadData";
	private static final String tagName = "PlayerPadData";
	public static final PlayerPadData get(EntityPlayer p) {
		return (PlayerPadData) p.getExtendedProperties(EXT_PROP_NAME);
	}


	public static final void register(EntityPlayer player) {
		if (player != null) {
			player.registerExtendedProperties(EXT_PROP_NAME, new PlayerPadData(player));
			FMLLog.getLogger().info("Player properties registered for Telepads");
		}
	}
	public EntityPlayer player;
	private ArrayList<int[]> allCoords = new ArrayList<int[]>();

	private ArrayList<Integer> allDims = new ArrayList<Integer>();

	private ArrayList<String> allNames = new ArrayList<String>();

	public PlayerPadData(EntityPlayer p) {
		if (p != null)
			player = p;
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

		for(int i = 0; i < allCoords.size(); i++)
			if(allCoords.get(i)[0] == a)
				if(allCoords.get(i)[1] == b)
					if(allCoords.get(i)[2] == c){
						allCoords.remove(i);
						allNames.remove(i);
						allDims.remove(i);
					}
		trim();
	}

	@Override
	public void saveNBTData(NBTTagCompound compound) {
		compound.setInteger("Size", allCoords.size());

		for(int i = 0; i < allCoords.size(); i++)
			compound.setIntArray("allCoords_"+i, allCoords.get(i));

		for(int i = 0; i < allDims.size(); i++)
			compound.setInteger("allDims_"+i, allDims.get(i));

		for(int i = 0; i < allNames.size(); i++)
			compound.setString("allNames_"+i, allNames.get(i));

		trim();

	}

	private void trim(){
		allCoords.trimToSize();
		allDims.trimToSize();
		allNames.trimToSize();
	}


	private static String getSaveKey(EntityPlayer player) {
		return player.getDisplayName() + ":" + EXT_PROP_NAME;
	}


	public static void saveProxyData(EntityPlayer player) {
		PlayerPadData playerData = PlayerPadData.get(player);
		NBTTagCompound savedData = new NBTTagCompound();

		playerData.saveNBTData(savedData);
		SProxy.storeEntityData(getSaveKey(player), savedData);
	}

	/**
	 * This cleans up the onEntityJoinWorld event by replacing most of the code
	 * with a single line: ExtendedPlayer.loadProxyData((EntityPlayer) event.entity));
	 */
	public static void loadProxyData(EntityPlayer player) {
		PlayerPadData playerData = PlayerPadData.get(player);
		NBTTagCompound savedData = SProxy.getEntityData(getSaveKey(player));

		if(savedData != null) {
			playerData.loadNBTData(savedData);
		}
//		playerData.syncClient(player);
	}
	
	
	private void syncClient(EntityPlayer p){
		ByteBuf buf = Unpooled.buffer();
		ByteBufOutputStream out = new ByteBufOutputStream(buf);

		try {

			out.writeInt(Serverpacket.SYNC);

			out.writeInt(0);
			out.writeInt(0);
			out.writeInt(0);

			out.writeInt(PlayerPadData.get(p).getAllCoords().size());

			for(int i = 0; i < PlayerPadData.get(p).getAllCoords().size(); i++){
				out.writeInt(PlayerPadData.get(p).getAllCoords().get(i)[0]);
				out.writeInt(PlayerPadData.get(p).getAllCoords().get(i)[1]);
				out.writeInt(PlayerPadData.get(p).getAllCoords().get(i)[2]);

				out.writeInt(PlayerPadData.get(p).getAllDims().get(i));
				out.writeUTF(PlayerPadData.get(p).getAllNames().get(i));

			}

			if(!p.worldObj.isRemote){
				Telepads.Channel.sendTo(new FMLProxyPacket(buf, Telepads.packetChannel), (EntityPlayerMP) p);
			System.out.println("packet send");
			}
			out.close();
		} catch (Exception e) {
		}
	}
}
