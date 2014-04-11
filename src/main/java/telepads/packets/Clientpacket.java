package telepads.packets;

import io.netty.buffer.ByteBufInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import telepads.block.TETelepad;
import telepads.util.PlayerPadData;
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


			case SYNC :

				int size = dis.readInt();

				PlayerPadData dat = PlayerPadData.get(player);
				for(int i = 0; i < size; i++){
					int x2 = dis.readInt();
					int y2 = dis.readInt();
					int z2 = dis.readInt();

					int[] a = new int[3];
					a[0]=x2; a[1]=y2; a[2]=z2;

					int dim = dis.readInt();
					String name = dis.readUTF();

//					if (dat.getAllCoords().get(i) == null){
						dat.getAllCoords().add(a);
						dat.getAllDims().add(dim);
						dat.getAllNames().add(name);
				}


				break;

			case SYNC_REGISTER:

				String name = dis.readUTF();

				pad.telepadname = name;

				int[] a = new int[3]; a[0] = pad.xCoord; a[1] = pad.yCoord; a[2] = pad.zCoord;

				PlayerPadData.get(player).getAllCoords().add(a);
				PlayerPadData.get(player).getAllNames().add(name);
				PlayerPadData.get(player).getAllDims().add(pad.dimension);

				break;

			default:
				break;
			}

			dis.close();
		} catch (Exception e) {
		}
	}
}
