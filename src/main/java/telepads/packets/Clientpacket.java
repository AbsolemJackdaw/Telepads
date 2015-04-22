package telepads.packets;

import io.netty.buffer.ByteBufInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import telepads.block.TETelepad;
import telepads.util.PlayerPadData;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;

public class Clientpacket extends Serverpacket {

	@SubscribeEvent
	public void onClientPacket(ClientCustomPacketEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		ByteBufInputStream dis = new ByteBufInputStream(event.packet.payload());

		try {

			int guiId = dis.readInt();

			int x = dis.readInt();
			int y = dis.readInt();
			int z = dis.readInt();

			TETelepad pad = (TETelepad) player.worldObj.getTileEntity(x, y, z);

			switch (guiId){

			case SYNCHRONIZE_DATA_OVER_CHANGE :

				int size = dis.readInt();

				PlayerPadData dat = PlayerPadData.get(player);
				for(int i = 0; i < size; i++){
					int x2 = dis.readInt();
					int y2 = dis.readInt();
					int z2 = dis.readInt();

					int[] coords = new int[]{x2, y2,z2};

					int dim = dis.readInt();
					String name = dis.readUTF();

					dat.getAllCoords().add(coords);
					dat.getAllDims().add(dim);
					dat.getAllNames().add(name);
				}
				FMLLog.getLogger().info("Client Packet " + SYNCHRONIZE_DATA_OVER_CHANGE + " processed");
				break;

			case ADD_TELEPAD_FOR_PLAYER:

				String name = dis.readUTF();

				pad.telepadname = name;
				pad.ownerName = player.getGameProfile().getName();
				pad.dimension = player.worldObj.provider.dimensionId;

				int[] a = new int[3]; a[0] = pad.xCoord; a[1] = pad.yCoord; a[2] = pad.zCoord;

				PlayerPadData.get(player).getAllCoords().add(a);
				PlayerPadData.get(player).getAllNames().add(name);
				PlayerPadData.get(player).getAllDims().add(pad.dimension);

				player.worldObj.markBlockForUpdate(pad.xCoord,pad.yCoord,pad.zCoord);

				FMLLog.getLogger().info("Client Packet " + ADD_TELEPAD_FOR_PLAYER + " processed");

				break;

			case PLATFORM:
				if(pad == null)
					break;
				pad.setStandingOnPlatform(dis.readBoolean());
				
				break;

			default:
				break;
			}

			dis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
