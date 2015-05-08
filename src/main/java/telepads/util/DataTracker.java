package telepads.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import telepads.Telepads;
import telepads.packets.PacketDataOverDimensions;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

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
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		PlayerPadData.get(e.player);
		syncClient(e.player);
	}


	private void syncClient(EntityPlayer p){
		if(p.worldObj.isRemote)
			return;

		for(int i = 0; i < PlayerPadData.get(p).getAllCoords().size(); i++){

			if(!p.worldObj.isRemote)
				Telepads.SNW.sendTo(
						new PacketDataOverDimensions(
								PlayerPadData.get(p).getAllCoords().get(i)[0],
								PlayerPadData.get(p).getAllCoords().get(i)[1],
								PlayerPadData.get(p).getAllCoords().get(i)[2],
								PlayerPadData.get(p).getAllDims().get(i),
								PlayerPadData.get(p).getAllNames().get(i)), 
								(EntityPlayerMP) p);
		}
	}
}
