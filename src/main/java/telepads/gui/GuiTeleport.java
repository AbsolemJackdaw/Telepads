package telepads.gui;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import telepads.Telepads;
import telepads.block.TETelepad;
import telepads.packets.Serverpacket;
import telepads.util.PlayerPadData;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class GuiTeleport extends GuiScreen{

	public EntityPlayer player;
	public TETelepad te;

	public static final int EXIT_BUTTON = 10000;

	private static final ResourceLocation enderPortalEndSkyTextures = new ResourceLocation("textures/environment/end_sky.png");

	private static final ResourceLocation endPortalTextures = new ResourceLocation("textures/entity/end_portal.png");

	float c = 0;

	float sd = 0;

	public GuiTeleport(EntityPlayer player, TETelepad te){
		Minecraft.getMinecraft().gameSettings.guiScale = 2;

		this.te = te;
		this.player = player;

	}

	@Override
	public void actionPerformed(GuiButton button) {

		if(player != null){
			int id = button.id;
			if(id == EXIT_BUTTON ){
				sendPacket(EXIT_BUTTON, button);
				this.mc.thePlayer.closeScreen(); //closes the screen

			}else{
				sendPacket(id, button);
				this.mc.thePlayer.closeScreen(); //closes the screen
			}
		}

		te.resetTE();
	}
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void drawBackground(int par1) {
		c += 1f;
		sd +=0.01f;
		float k = c+2;

		GL11.glPushMatrix();
		GL11.glColor4f(0.2f, 0.6f, 1f, sd < 0.7f ? sd : 0.7f);
		Minecraft.getMinecraft().renderEngine.bindTexture(enderPortalEndSkyTextures);
		drawTexturedModalRect(0, 0, -(int)k*2, -(int)c*2 , 3000, 3000);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		GL11.glColor4f(0.2f, 0.6f, 1f, sd < 0.75f ? sd : 0.75f);
		Minecraft.getMinecraft().renderEngine.bindTexture(endPortalTextures);
		drawTexturedModalRect(0, 0, (int)k*2, (int)c*2 , 3000, 3000);
		GL11.glPopMatrix();

	}
	@Override
	public void drawScreen(int par1, int par2, float par3) {

		this.drawBackground(par1);
		super.drawScreen(par1, par2, par3);
	}

	@Override
	public void initGui() {

		this.buttonList.clear();

		this.buttonList.add(new GuiButton(EXIT_BUTTON, 5, 5, 20, 20, "X"));


		int c = PlayerPadData.get(player).getAllCoords().size() ;
		//		System.out.println(PlayerPadData.get(player).getAllCoords() +
		//				"\n" + PlayerPadData.get(player).getAllDims() +
		//				"\n" + PlayerPadData.get(player).getAllNames());
		if(c < 1) {
			return;
		}

		for(int i = 0; i < c; i++){
			String name = PlayerPadData.get(player).getAllNames().get(i);

			this.buttonList.add(new GuiButton(i, /*x*/(40) + (((i/10) > 0) && ((i%10) >= 0) ? 120*(i/10) : 0),/*y*/(130 + ((i*25))) - (((i/10) > 0) && ((i%10) >= 0) ? (250*(i/10))+100 : 100),
					/*size*/100, 20, /**/name));
		}
	}

	@Override
	protected void keyTyped(char c, int i)
	{
		super.keyTyped(c, i);

		if(i == Keyboard.KEY_ESCAPE){
			te.resetTE();
			mc.thePlayer.closeScreen();
		}
	}

	@Override
	protected void mouseClicked(int i, int j, int k)
	{
		super.mouseClicked(i, j, k);
	}


	public void sendPacket(int id, GuiButton button){
		ByteBuf buf = Unpooled.buffer();
		ByteBufOutputStream out = new ByteBufOutputStream(buf);

		try {

			out.writeInt(Serverpacket.TELEPORT); //TODO a packet int here
			out.writeInt(te.xCoord);
			out.writeInt(te.yCoord);
			out.writeInt(te.zCoord);

			if(button.id < EXIT_BUTTON){
				out.writeInt(PlayerPadData.get(player).getAllDims().get(button.id)); //other pad dimension

				out.writeInt(PlayerPadData.get(player).getAllCoords().get(button.id)[0]); //other pad x
				out.writeInt(PlayerPadData.get(player).getAllCoords().get(button.id)[1]); //other pad y
				out.writeInt(PlayerPadData.get(player).getAllCoords().get(button.id)[2]); //other pad z
				Telepads.Channel.sendToServer(new FMLProxyPacket(buf, Telepads.packetChannel));
			}

			out.close();
		} catch (Exception e) {
		}

	}
}
