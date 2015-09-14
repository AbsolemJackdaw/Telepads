package net.subaraki.telepads.client.gui;

import org.lwjgl.input.Keyboard;

import net.darkhax.bookshelf.util.Position;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import net.subaraki.telepads.Telepads;
import net.subaraki.telepads.common.network.PacketAddTelepadEntry;
import net.subaraki.telepads.handler.PlayerLocations.TelepadEntry;
import net.subaraki.telepads.tileentity.TileEntityTelepad;

public class GuiNameTelepad extends GuiScreen {
    
    private GuiTextField padNameField;
    
    public TileEntityTelepad te;
    
    public GuiNameTelepad(EntityPlayer player, TileEntityTelepad te) {
        this.te = te;
    }
    
    @Override
    public void actionPerformed (GuiButton button) {
    
    }
    
    @Override
    public boolean doesGuiPauseGame () {
        
        return false;
    }
    
    @Override
    public void drawScreen (int par1, int par2, float par3) {
        
        int posX = (this.width) / 2;
        int posY = (this.height) / 2;
        try {
            
            String p = StatCollector.translateToLocal("gui.telepad.confrim");
            
            fontRendererObj.drawSplitString(p, (posX + 1) - 75, posY - 1, 180, 0x000000);
            fontRendererObj.drawSplitString(p, posX - 75, posY, 180, 0xffffff);
            
            String q = StatCollector.translateToLocal("gui.telepad.name");
            
            fontRendererObj.drawSplitString(q + " : " + padNameField.getText(), (posX + 1) - 75, posY - 1 - 20, 180, 0x000000);
            fontRendererObj.drawSplitString(q + " : " + padNameField.getText(), posX - 75, posY - 20, 180, 0xff0000);
            
        }
        finally {
            if (padNameField != null) {
                padNameField.drawTextBox();
            }
        }
    }
    
    @Override
    public void initGui () {
        
        int posX = (this.width) / 2;
        int posY = (this.height) / 2;
        this.buttonList.clear();
        
        padNameField = new GuiTextField(fontRendererObj, posX - (150 / 2), posY - 50, 150, 20);
        padNameField.setFocused(true);
        
        String padName = te.telepadname.equals("TelePad") ? te.getWorldObj().getBiomeGenForCoords(te.xCoord, te.zCoord).biomeName : te.telepadname;
        
        if (padNameField != null) {
            padNameField.setText(padName);
            padNameField.setMaxStringLength(50);
        }
    }
    
    @Override
    protected void keyTyped (char c, int i) {
        
        super.keyTyped(c, i);
        
        if (i == Keyboard.KEY_RETURN || i == Keyboard.KEY_ESCAPE) {
            sendPacket(padNameField.getText());
        }
        
        if (padNameField != null) {
            padNameField.textboxKeyTyped(c, i);
        }
    }
    
    @Override
    protected void mouseClicked (int i, int j, int k) {
        
        super.mouseClicked(i, j, k);
        if (padNameField != null) {
            padNameField.mouseClicked(i, j, k);
        }
    }
    
    public void sendPacket (String padName) {
        
        Telepads.instance.network.sendToServer(new PacketAddTelepadEntry(mc.thePlayer.getUniqueID(), new TelepadEntry(padNameField.getText(), mc.thePlayer.worldObj.provider.dimensionId, new Position(te.xCoord, te.yCoord, te.zCoord))));
        
        this.mc.thePlayer.closeScreen();
        
    }
    
}
