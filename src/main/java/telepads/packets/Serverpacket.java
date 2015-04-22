package telepads.packets;

import com.sun.corba.se.impl.ior.ByteBuffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import telepads.Telepads;
import telepads.block.TETelepad;
import telepads.util.PlayerPadData;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class Serverpacket {


	public static final int ADD_TELEPAD_FOR_PLAYER = 51;
	public static final int TELEPORT = 52;
	public static final int SYNCHRONIZE_DATA_OVER_CHANGE = 54;
	public static final int PLATFORM = 55;

	@SubscribeEvent
	public void onServerPacket(ServerCustomPacketEvent event) {

		EntityPlayerMP player = ((NetHandlerPlayServer)event.handler).playerEntity;
		ByteBufInputStream dis = new ByteBufInputStream(event.packet.payload());
		ByteBuf buf = event.packet.payload();

		try {

			int guiId = dis.readInt();

			int x = dis.readInt();
			int y = dis.readInt();
			int z = dis.readInt();

			TETelepad pad = (TETelepad) player.worldObj.getTileEntity(x, y, z);

			switch (guiId) {

			case ADD_TELEPAD_FOR_PLAYER:

				String name = dis.readUTF();

				pad.telepadname = name;
				pad.ownerName = player.getGameProfile().getName();
				pad.dimension = player.worldObj.provider.dimensionId;

				int[] coords = new int[]{pad.xCoord, pad.yCoord, pad.zCoord};

				PlayerPadData.get(player).getAllCoords().add(coords);
				PlayerPadData.get(player).getAllNames().add(name);
				PlayerPadData.get(player).getAllDims().add(pad.dimension);

				player.worldObj.markBlockForUpdate(pad.xCoord, pad.yCoord, pad.zCoord);

				try {
					/**Dont be stupid and do this EVER again you kunt ... 
					 * > forgot to change buf into buffer for output...*/
//					ByteBufOutputStream out = new ByteBufOutputStream(buf);
//					ByteBuf buffer = Unpooled.buffer();
					
					ByteBuf buffer = Unpooled.buffer();
					ByteBufOutputStream out = new ByteBufOutputStream(buffer);
					
					out.writeInt(Serverpacket.ADD_TELEPAD_FOR_PLAYER);
					out.writeInt(pad.xCoord);
					out.writeInt(pad.yCoord);
					out.writeInt(pad.zCoord);
					out.writeUTF(pad.telepadname);

					Telepads.Channel.sendTo(new FMLProxyPacket(buffer, Telepads.packetChannel), player);

					out.close();
				} catch (Exception e){
					e.printStackTrace();
				}
				
				FMLLog.getLogger().info("Server Packet " + ADD_TELEPAD_FOR_PLAYER + " processed");
				break;

			case TELEPORT:
				int dimID = dis.readInt();

				int otherX = dis.readInt();
				int otherY = dis.readInt();
				int otherZ = dis.readInt();

				if(dimID != player.worldObj.provider.dimensionId){
					if(player.worldObj.provider.dimensionId == 1) {
						player.travelToDimension(1);
					}else{
						player.travelToDimension(dimID);
						player.setPositionAndUpdate(otherX+2, otherY+0.5d, otherZ);
					}

				} else {
					player.setPositionAndUpdate(otherX+2, otherY+0.5d, otherZ);
				}
				FMLLog.getLogger().info("Client Packet " + TELEPORT + " processed");
				break;

			case PLATFORM:

				boolean b = dis.readBoolean();
				
				if(pad == null)
					break;
				
				try {
					ByteBuf buffer = Unpooled.buffer();
					ByteBufOutputStream out = new ByteBufOutputStream(buffer);

					out.writeInt(PLATFORM);
					out.writeInt(pad.xCoord);
					out.writeInt(pad.yCoord);
					out.writeInt(pad.zCoord);
					out.writeBoolean(b);

					Telepads.Channel.sendTo(new FMLProxyPacket(buffer, Telepads.packetChannel), player);

					out.close();
				} catch (Exception e) {
					e.printStackTrace();
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
