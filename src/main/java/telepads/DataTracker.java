package telepads;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import telepads.util.PlayerPadData;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class DataTracker {


	@SubscribeEvent
	public void onPlayerChangedDimension(PlayerChangedDimensionEvent e) {
		PlayerPadData.get(e.player);
		syncClient(e.player);
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent e) {
		PlayerPadData.get(e.player);
		syncClient(e.player);
	}

	@SubscribeEvent
	public void onPlayerLogout(PlayerLoggedOutEvent e) {
		//		PlayerPadData.get(e.player);
		//		syncClient(e.player);
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		PlayerPadData.get(e.player);
		syncClient(e.player);
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
