package telepads.gui;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;

import telepads.Serverpacket;
import telepads.Telepads;
import telepads.block.TETelepad;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class GuiNameTelepad extends GuiScreen{

	private GuiTextField padNameField;

	public EntityPlayer thePlayer;
	public TETelepad te;

	public GuiNameTelepad(EntityPlayer player, TETelepad te){
		thePlayer = player;
		this.te = te;
	}

	@Override
	public void actionPerformed(GuiButton button) {
	}


	@Override
	public boolean doesGuiPauseGame(){
		return false;
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {

		int posX = (this.width ) / 2;
		int posY = (this.height ) / 2;
		try{
			fontRendererObj.drawSplitString("Press Enter to confirm", (posX+1) -75, posY-1, 180 ,0x000000);
			fontRendererObj.drawSplitString("Press Enter to confirm", posX -75, posY, 180 ,0xffffff);

			fontRendererObj.drawSplitString("Name Your TelePad : "+padNameField.getText(), (posX+1) -75 -100, posY-1-20, 180 ,0x000000);
			fontRendererObj.drawSplitString("Name Your TelePad : "+padNameField.getText(), posX   -75 -100, posY  -20, 180 ,0xff0000);

		}finally{
			if(padNameField != null) padNameField.drawTextBox();
		}

	}

	@Override
	public void initGui() {

		int posX = (this.width ) / 2;
		int posY = (this.height ) / 2;
		this.buttonList.clear();

		padNameField = new GuiTextField(fontRendererObj, posX-(150/2)    -100 , posY-50, 150, 20);

		String padName = te.telepadname.equals("TelePad") ? te.getWorldObj().getBiomeGenForCoords(te.xCoord, te.zCoord).biomeName : te.telepadname;

		if(padNameField != null){
			padNameField.setText(padName);
			padNameField.setMaxStringLength(50);
		}

	}

	@Override
	protected void keyTyped(char c, int i)
	{
		super.keyTyped(c, i);
		if(i == Keyboard.KEY_RETURN)
			sendPacket(padNameField.getText());

		if(padNameField != null)
			padNameField.textboxKeyTyped(c, i);
	}


	@Override
	protected void mouseClicked(int i, int j, int k)
	{
		super.mouseClicked(i, j, k);
		if(padNameField != null)
			padNameField.mouseClicked(i, j, k);
	}

	public void sendPacket(String padName){

		ByteBuf buf = Unpooled.buffer();
		ByteBufOutputStream out = new ByteBufOutputStream(buf);

		try {
			out.writeInt(Serverpacket.SYNC_REGISTER);

			out.writeInt(te.xCoord);
			out.writeInt(te.yCoord);
			out.writeInt(te.zCoord);

			out.writeUTF(padName);

			Telepads.Channel.sendToServer(new FMLProxyPacket(buf, Telepads.packetChannel));
			out.close();
		} catch (Exception e) {
		}

		this.mc.thePlayer.closeScreen();
	}
}
