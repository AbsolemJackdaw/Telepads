package net.subaraki.telepads.common;

import net.minecraft.world.World;

public class CommonProxy {
    
    /**
     * A sided version of the preInit event. This method allows for code to be executed
     * exclusively on the client or server side during the pre-initialization phase.
     */
    public void preInit () {
    
    }
    
    /**
     * Creates a particle effect which is used when the player activates a Telepad.
     * 
     * @param x : The X coordinate of the Telepad.
     * @param y : The Y coordinate of the Telepad.
     * @param z : The Z coordinate of the Telepad.
     */
    public void createTelepadParticleEffect (int x, int y, int z, boolean isStandingOnPlate) {
    
    }
    
    public World getClientWorld () {
        
        return null;
    }
}
