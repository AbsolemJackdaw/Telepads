package net.subaraki.telepads.client.gui;

import net.darkhax.bookshelf.util.Position;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import net.subaraki.telepads.handler.PlayerLocations;
import net.subaraki.telepads.handler.PlayerLocations.TelepadEntry;
import net.subaraki.telepads.tileentity.TileEntityTelepad;

import com.mojang.realmsclient.gui.ChatFormatting;

public class GuiRemovePad extends GuiScreen{

	private GuiTextField padNameField;

	public TileEntityTelepad te;
	private EntityPlayer player;

	private Position positionToRemove;

	public GuiRemovePad(EntityPlayer player, TileEntityTelepad telepad) {
		this.player = player;
	}

	public GuiScreen setPositionToRemove(Position pos){
		positionToRemove = pos;
		return this;
	}

	@Override
	public void actionPerformed (GuiButton button) {
		if(button.id == 0){
			//remove position from player entry
			//remove position from any other player entry ?
			//send packet to do so
		}
	}

	@Override
	public boolean doesGuiPauseGame () {

		return false;
	}

	@Override
	public void drawScreen (int par1, int par2, float par3) {

		int posX = (this.width) / 2;
		int posY = (this.height) / 2;

		String s = StatCollector.translateToLocal("cannot.find.remove?");

		fontRendererObj.drawSplitString(s, (posX + 1) - 75, posY - 1, 180, 0x000000);
		fontRendererObj.drawSplitString(s, posX - 75, posY, 180, 0xffffff);
	}

	@Override
	public void initGui () {

		int posX = (this.width) / 2;
		int posY = (this.height) / 2;

		this.buttonList.clear();

		this.buttonList.add(new GuiButton(0, 20, 100, 20, 20, ChatFormatting.RED + "X"));

		PlayerLocations pl = PlayerLocations.getProperties(player);
		
		for(TelepadEntry tpe : pl.getEntries()){
			if(tpe.position.equals(positionToRemove)){
				this.buttonList.add(new GuiButton(1, 50, 100, 100, 20, tpe.entryName));
				break;
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
