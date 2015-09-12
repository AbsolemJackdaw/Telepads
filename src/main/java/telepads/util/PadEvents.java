package telepads.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PadEvents {

	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event) {
		if (!(event.entity instanceof EntityPlayer))
			return;
		EntityPlayer p = (EntityPlayer) event.entity;

		if ((PlayerPadData.get(p) == null)) {
			PlayerPadData.register(p);
			FMLLog.getLogger()
					.info("initiated new save properties for player.");
		}
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if (!event.entity.worldObj.isRemote
				&& (event.entity instanceof EntityPlayer)) {
			PlayerPadData.loadProxyData((EntityPlayer) event.entity);
		}
	}

	@SubscribeEvent
	public void onLivingDeathEvent(LivingDeathEvent event) {
		// we only want to save data for players (most likely, anyway)
		if (!event.entity.worldObj.isRemote
				&& (event.entity instanceof EntityPlayer)) {
			PlayerPadData.saveProxyData((EntityPlayer) event.entity);
		}
	}
}
