package net.subaraki.telepads.client.gui;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.darkhax.bookshelf.lib.Position;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import net.subaraki.telepads.Telepads;
import net.subaraki.telepads.common.network.PacketRemoveTelepadEntry;
import net.subaraki.telepads.common.network.PacketTeleport;
import net.subaraki.telepads.handler.PlayerLocations;
import net.subaraki.telepads.handler.PlayerLocations.TelepadEntry;
import net.subaraki.telepads.tileentity.TileEntityTelepad;

public class GuiRemovePad extends GuiScreen {
    
    private GuiTextField padNameField;
    
    public TileEntityTelepad te;
    private EntityPlayer player;
    
    private TelepadEntry entryToRemove;
    
    public GuiRemovePad(EntityPlayer player, TileEntityTelepad telepad) {
        super();
        this.player = player;
        this.te = telepad;
    }
    
    public GuiRemovePad setEntryToRemove (TelepadEntry tpe) {
        
        entryToRemove = tpe;
        return this;
    }
    
    @Override
    public void actionPerformed (GuiButton button) {
        
        if (button.id == 0) {
            Telepads.instance.network.sendToServer(new PacketRemoveTelepadEntry(player.getPersistentID(), entryToRemove));
            this.mc.thePlayer.closeScreen();
        }
        
        if (button.id == 1) {
            Telepads.instance.network.sendToServer(new PacketTeleport(entryToRemove.position, entryToRemove.dimensionID, new Position(te.xCoord, te.yCoord, te.zCoord), true));
            this.mc.thePlayer.closeScreen();
        }
    }
    
    @Override
    public boolean doesGuiPauseGame () {
        
        return false;
    }
    
    @Override
    public void drawScreen (int par1, int par2, float par3) {
        
        super.drawScreen(par1, par2, par3);
        
        int posX = (this.width) / 2;
        int posY = (this.height) / 2;
        
        String s = StatCollector.translateToLocal("cannot.find.remove?");
        
        fontRendererObj.drawSplitString(s, (posX + 1) - 75, posY - 1, 180, 0x000000);
        fontRendererObj.drawSplitString(s, posX - 75, posY, 180, 0xffffff);
    }
    
    @Override
    public void initGui () {
        
        super.initGui();
        
        int posX = (this.width) / 2;
        int posY = (this.height) / 2;
        
        this.buttonList.clear();
        
        this.buttonList.add(new GuiButton(0, posX - 65, posY - 25, 20, 20, ChatFormatting.RED + "X"));
        
        Telepads.printDebugMessage(buttonList + "");
        PlayerLocations pl = PlayerLocations.getProperties(player);
        
        for (TelepadEntry tpe : pl.getEntries()) {
            Telepads.printDebugMessage(tpe.entryName);
            if (tpe.position.equals(entryToRemove.position)) {
                if (tpe.dimensionID == entryToRemove.dimensionID) {
                    this.buttonList.add(new GuiButton(1, posX - 35, posY - 25, 100, 20, tpe.entryName));
                    break;
                }
            }
        }
    }
    
    @Override
    protected void keyTyped (char c, int i) {
        
        super.keyTyped(c, i);
        
    }
    
    public void sendPacket (String padName) {
        
        this.mc.thePlayer.closeScreen();
        
    }
    
}
