package telepads;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import telepads.util.PlayerPadData;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PadEvents {

	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event) {
		/*
		 * Be sure to check if the entity being constructed is the correct type
		 * for the extended properties you're about to add!The null check may
		 * not be necessary - I only use it to make sure properties are only
		 * registered once per entity
		 */
		if ((event.entity instanceof EntityPlayer)
				&& (PlayerPadData.get((EntityPlayer) event.entity) == null))
			// This is how extended properties are registered using our
			// convenient method from earlier
			PlayerPadData.register((EntityPlayer) event.entity);
	}

	@SubscribeEvent
	public void onLivingDeathEvent(LivingDeathEvent event)
	{
		// we only want to save data for players (most likely, anyway)
		if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer){
			PlayerPadData.saveProxyData((EntityPlayer) event.entity);
		}
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer){
			PlayerPadData.loadProxyData((EntityPlayer) event.entity);
		}
	}


}
