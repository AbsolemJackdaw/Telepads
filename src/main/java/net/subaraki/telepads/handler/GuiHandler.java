package net.subaraki.telepads.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.subaraki.telepads.client.gui.GuiNameTelepad;
import net.subaraki.telepads.client.gui.GuiTeleport;
import net.subaraki.telepads.inventory.ContainerTelePad;
import net.subaraki.telepads.tileentity.TileEntityTelepad;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
    
    @Override
    public Object getServerGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z) {
        
        switch (ID) {
            case 0:
                return new ContainerTelePad();
        }
        
        return null;
    }
    
    @Override
    public Object getClientGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z) {
        
        TileEntityTelepad te = (TileEntityTelepad) world.getTileEntity(x, y, z);
        
        switch (ID) {
            case 0:
                return new GuiTeleport(player, te);
            case 1:
                return new GuiNameTelepad(player, te);
        }
        
        return null;
    }
}