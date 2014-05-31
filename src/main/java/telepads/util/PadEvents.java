package telepads.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PadEvents {

	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event) {
		if ((event.entity instanceof EntityPlayer)
				&& (PlayerPadData.get((EntityPlayer) event.entity) == null)) {
			PlayerPadData.register((EntityPlayer) event.entity);
		}
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		if (!event.entity.worldObj.isRemote && (event.entity instanceof EntityPlayer)) {
			PlayerPadData.loadProxyData((EntityPlayer) event.entity);
		}
	}

	@SubscribeEvent
	public void onLivingDeathEvent(LivingDeathEvent event)
	{
		// we only want to save data for players (most likely, anyway)
		if (!event.entity.worldObj.isRemote && (event.entity instanceof EntityPlayer)) {
			PlayerPadData.saveProxyData((EntityPlayer) event.entity);
		}
	}


}
