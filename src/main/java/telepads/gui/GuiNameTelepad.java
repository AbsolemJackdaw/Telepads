package telepads.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;

import telepads.Telepads;
import telepads.block.TETelepad;
import telepads.packets.PacketAddTelepadForPlayer;

public class GuiNameTelepad extends GuiScreen {

	private GuiTextField padNameField;

	public EntityPlayer thePlayer;
	public TETelepad te;

	public GuiNameTelepad(EntityPlayer player, TETelepad te) {
		thePlayer = player;
		this.te = te;
	}

	@Override
	public void actionPerformed(GuiButton button) {
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {

		int posX = (this.width) / 2;
		int posY = (this.height) / 2;
		try {

			String p = StatCollector.translateToLocal("enter.to.confirm");

			fontRendererObj.drawSplitString(p, (posX + 1) - 75, posY - 1, 180,
					0x000000);
			fontRendererObj.drawSplitString(p, posX - 75, posY, 180, 0xffffff);

			String q = StatCollector.translateToLocal("name.your.telepad");

			fontRendererObj.drawSplitString(q + " : " + padNameField.getText(),
					(posX + 1) - 75, posY - 1 - 20, 180, 0x000000);
			fontRendererObj.drawSplitString(q + " : " + padNameField.getText(),
					posX - 75, posY - 20, 180, 0xff0000);

		} finally {
			if (padNameField != null) {
				padNameField.drawTextBox();
			}
		}
	}

	@Override
	public void initGui() {

		int posX = (this.width) / 2;
		int posY = (this.height) / 2;
		this.buttonList.clear();

		padNameField = new GuiTextField(fontRendererObj, posX - (150 / 2),
				posY - 50, 150, 20);
		padNameField.setFocused(true);

		String padName = te.telepadname.equals("TelePad") ? te.getWorldObj()
				.getBiomeGenForCoords(te.xCoord, te.zCoord).biomeName
				: te.telepadname;

		if (padNameField != null) {
			padNameField.setText(padName);
			padNameField.setMaxStringLength(50);
		}
	}

	@Override
	protected void keyTyped(char c, int i) {
		super.keyTyped(c, i);

		if (i == Keyboard.KEY_RETURN || i == Keyboard.KEY_ESCAPE) {
			sendPacket(padNameField.getText());
		}

		if (padNameField != null) {
			padNameField.textboxKeyTyped(c, i);
		}
	}

	@Override
	protected void mouseClicked(int i, int j, int k) {
		super.mouseClicked(i, j, k);
		if (padNameField != null) {
			padNameField.mouseClicked(i, j, k);
		}
	}

	public void sendPacket(String padName) {

		Telepads.SNW.sendToServer(new PacketAddTelepadForPlayer(te.xCoord,
				te.yCoord, te.zCoord, padName));

		this.mc.thePlayer.closeScreen();

	}

}
