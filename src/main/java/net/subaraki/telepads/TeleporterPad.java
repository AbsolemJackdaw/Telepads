package net.subaraki.telepads;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TeleporterPad extends Teleporter {

	private WorldServer worldServerInstance;

	public TeleporterPad(WorldServer worldServer) {
		super(worldServer);

		this.worldServerInstance = worldServer;
	}

	@Override
	public void placeInPortal (Entity entity, double posX, double posY, double posZ, float yaw) {
		entity.motionX = entity.motionY = entity.motionZ = 0.0D;
		entity.setLocationAndAngles(posX, posY, posZ, entity.rotationYaw, entity.rotationPitch);
	}
}
