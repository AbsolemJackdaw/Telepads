package net.subaraki.telepads.client.gui;

import java.util.List;

import net.darkhax.bookshelf.util.Position;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldProvider;
import net.subaraki.telepads.Telepads;
import net.subaraki.telepads.common.network.PacketTeleport;
import net.subaraki.telepads.handler.PlayerLocations;
import net.subaraki.telepads.handler.PlayerLocations.TelepadEntry;
import net.subaraki.telepads.tileentity.TileEntityTelepad;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class GuiTeleport extends GuiScreen {

	public EntityPlayer player;
	public TileEntityTelepad te;

	public static final int EXIT_BUTTON = 4000;
	public static final int AREA_LEFT = 3999;
	public static final int AREA_RIGHT = 3998;

	private int dimension_counter;

	private TextureManager renderEngine = Minecraft.getMinecraft().renderEngine;

	private static final ResourceLocation enderPortalEndSkyTextures = new ResourceLocation("textures/environment/end_sky.png");

	private static final ResourceLocation endPortalTextures = new ResourceLocation("textures/entity/end_portal.png");

//    private static final ResourceLocation fieldTexture = new ResourceLocation("textures/gui/widgets.png");

	private float c = 0;

	float sd = 0;

	public GuiTeleport(EntityPlayer player, TileEntityTelepad te) {

		this.te = te;
		this.player = player;
		dimension_counter = player.worldObj.provider.dimensionId;
	}

	@Override
	public void actionPerformed (GuiButton button) {

		if (player != null) {
			int id = button.id;

			if (id == EXIT_BUTTON)
				this.mc.thePlayer.closeScreen(); // closes the screen
			
			else if (id == AREA_LEFT) {
				dimension_counter--;
				drawButtonsOnScreen();
				Telepads.printDebugMessage("" + dimension_counter);
			}
			
			else if (id == AREA_RIGHT) {
				dimension_counter++;
				drawButtonsOnScreen();
				Telepads.printDebugMessage("" + dimension_counter);
			}
			
			else {
				sendPacket(id);
				this.mc.thePlayer.closeScreen();
			}
		}

		te.resetTE();
	}

	@Override
	public boolean doesGuiPauseGame () {

		return false;
	}

	@Override
	public void drawBackground (int par1) {

		c += 1f;
		sd += 0.01f;
		float k = c + 2;

		GL11.glPushMatrix();
		GL11.glColor4f(0.2f, 0.6f, 1f, sd < 0.7f ? sd : 0.7f);
		renderEngine.bindTexture(enderPortalEndSkyTextures);
		drawTexturedModalRect(0, 0, -(int) k * 2, -(int) c * 2, 3000, 3000);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		GL11.glColor4f(0.2f, 0.6f, 1f, sd < 0.75f ? sd : 0.75f);
		renderEngine.bindTexture(endPortalTextures);
		drawTexturedModalRect(0, 0, (int) k * 2, (int) c * 2, 3000, 3000);
		GL11.glPopMatrix();

	}

	@Override
	public void drawScreen (int par1, int par2, float par3) {

		this.drawBackground(par1);
		super.drawScreen(par1, par2, par3);

		List<TelepadEntry> telepads= PlayerLocations.getProperties(player).getEntries();

		 drawRect(46 - 1, 7 - 1, 174 + 1, 23 + 1, -6250336);
         drawRect(46, 7, 174, 23, -16777216);
    
         
		if(!te.hasDimensionUpgrade()){
			fontRendererObj.drawSplitString(te.getWorldObj().provider.getDimensionName(), 48+1, 11+1, 180, 0x000000);
			fontRendererObj.drawSplitString(te.getWorldObj().provider.getDimensionName(), 48, 11, 180, 0xffffff);
		}else{
			WorldProvider world = null;

			try{
				world = te.getWorldObj().provider.getProviderForDimension(dimension_counter);
			}catch (Exception e){
			}

			if(world != null){
				fontRendererObj.drawSplitString(world.getDimensionName(), 48 + 1 ,11 + 1, 180, 0x000000);
				fontRendererObj.drawSplitString(world.getDimensionName(), 48 , 11, 180, 0xffffff);
			}else{
				fontRendererObj.drawSplitString("No%Dim- Error : Hz " + dimension_counter, 48 + 1, 11 + 1, 180, 0x000000);
				fontRendererObj.drawSplitString("No%Dim- Error : Hz " + dimension_counter, 48, 11, 180, 0xffffff);
			}
		}
	}

	public void drawButtonsOnScreen(){
		
		this.buttonList.clear();

		if(te.hasDimensionUpgrade()){
			this.buttonList.add(new GuiButton(AREA_LEFT, 25, 5, 20, 20, "<"));
			this.buttonList.add(new GuiButton(AREA_RIGHT, 175, 5, 20, 20, ">"));
		}
		
		this.buttonList.add(new GuiButton(EXIT_BUTTON, 5, 5, 20, 20, "X"));

		List<TelepadEntry> telepads= PlayerLocations.getProperties(player).getEntries();

		int c = telepads.size();

		if (c < 1)
			return;

		for (int i = 0; i < c; i++) {
			String name = telepads.get(i).entryName;

			if(telepads.get(i).dimensionID == dimension_counter)
				this.buttonList.add(new GuiButton(/* id */i, /* x */(40) + (((i / 10) > 0) && ((i % 10) >= 0) ? 120 * (i / 10) : 0), /* y */(130 + ((i * 25))) - (((i / 10) > 0) && ((i % 10) >= 0) ? (250 * (i / 10)) + 100 : 100), /* size */100, 20, name));
		}
	}
	
	@Override
	public void initGui () {

		drawButtonsOnScreen();
		
		if(te.hasDimensionUpgrade()){
			this.buttonList.add(new GuiButton(AREA_LEFT, 25, 5, 20, 20, "<"));
			this.buttonList.add(new GuiButton(AREA_RIGHT, 175, 5, 20, 20, ">"));
		}
	}

	@Override
	protected void keyTyped (char c, int i) {

		super.keyTyped(c, i);

		if (i == Keyboard.KEY_ESCAPE) {
			te.resetTE();
			mc.thePlayer.closeScreen();
		}
	}

	@Override
	protected void mouseClicked (int i, int j, int k) {

		super.mouseClicked(i, j, k);
	}

	public void sendPacket (int id) {

		if (player == null)
			return;

		System.out.println(player.getUniqueID());

		int x = PlayerLocations.getProperties(player).getEntries().get(id).position.getX();
		int y = PlayerLocations.getProperties(player).getEntries().get(id).position.getY();
		int z = PlayerLocations.getProperties(player).getEntries().get(id).position.getZ();
		int dim = PlayerLocations.getProperties(player).getEntries().get(id).dimensionID;

		Telepads.instance.network.sendToServer(new PacketTeleport(new Position(x, y, z), dim));
	}
}
