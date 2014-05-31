package telepads.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import telepads.Telepads;
import telepads.block.TETelepad;
import telepads.util.PlayerPadData;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class Serverpacket {


	public static final int SYNC_REGISTER = 5100;
	public static final int TELEPORT = 5200;
	public static final int SYNC = 5400;
	public static final int REGISTER = 5500;

	@SubscribeEvent
	public void onServerPacket(ServerCustomPacketEvent event) {

		EntityPlayerMP player = ((NetHandlerPlayServer)event.handler).playerEntity;
		ByteBufInputStream dis = new ByteBufInputStream(event.packet.payload());
		ByteBuf buf = event.packet.payload();

		System.out.println("read packet");

		try {

			int guiId = dis.readInt();

			int x = dis.readInt();
			int y = dis.readInt();
			int z = dis.readInt();

			TETelepad pad = (TETelepad) player.worldObj.getTileEntity(x, y, z);

			switch (guiId) {



			case SYNC_REGISTER:

				String name = dis.readUTF();

				pad.telepadname = name;
				pad.addRegister();

				int[] a = new int[3]; a[0] = pad.xCoord; a[1] = pad.yCoord; a[2] = pad.zCoord;

				PlayerPadData.get(player).getAllCoords().add(a);
				PlayerPadData.get(player).getAllNames().add(name);
				PlayerPadData.get(player).getAllDims().add(pad.dimension);

				Telepads.Channel.sendTo(new FMLProxyPacket(buf, Telepads.packetChannel), player);

				player.worldObj.markBlockForUpdate(pad.xCoord, pad.yCoord, pad.zCoord);

				break;

			case TELEPORT:
				int dimID = dis.readInt();

				int otherX = dis.readInt();
				int otherY = dis.readInt();
				int otherZ = dis.readInt();

				if(dimID != player.worldObj.provider.dimensionId){
					if(player.worldObj.provider.dimensionId == 1) {
						player.travelToDimension(1);
					} else{
						player.travelToDimension(dimID);
						player.setPositionAndUpdate(otherX+2, otherY+0.5d, otherZ);
					}

				} else {
					player.setPositionAndUpdate(otherX+2, otherY+0.5d, otherZ);
				}

				break;

			default:
				break;
			}

			dis.close();
		} catch (Exception e) {
		}
	}

}
