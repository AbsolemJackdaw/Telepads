package net.subaraki.telepads.client.renderer;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.subaraki.telepads.client.model.ModelTelepad;
import net.subaraki.telepads.tileentity.TileEntityTelepad;

import org.lwjgl.opengl.GL11;

public class RenderTileEntityTelepad extends TileEntitySpecialRenderer {

	private static String resourcePath = "telepads:textures/entity/tile/";
	private static ResourceLocation base = new ResourceLocation(resourcePath
			+ "telepad_base.png");
	private static ResourceLocation pads = new ResourceLocation(resourcePath
			+ "telepad_pads.png");
	private static ResourceLocation frame = new ResourceLocation(resourcePath
			+ "telepad_frame.png");
	private static ResourceLocation frame_empty = new ResourceLocation(
			resourcePath + "telepad_frame_interDimension.png");
	private static ResourceLocation frame_upgrade = new ResourceLocation(
			resourcePath + "telepad_dimensionUpgrade.png");
	private static ResourceLocation frame_upgrade_2 = new ResourceLocation(
			resourcePath + "telepad_dimensionUpgrade_2.png");
	private static ResourceLocation frame_upgrade_3 = new ResourceLocation(
			resourcePath + "telepad_dimensionUpgrade_3.png");
	private static ResourceLocation frame_upgrade_4 = new ResourceLocation(
			resourcePath + "telepad_dimensionUpgrade_4.png");
	private static ResourceLocation frame_powered_off = new ResourceLocation(
			"minecraft:textures/blocks/red_sand.png");
	private static ResourceLocation frame_powered_on = new ResourceLocation(
			"minecraft:textures/blocks/redstone_block.png");

	private static int animation_counter;

	// copied from RenderEndPortal.java
	private static final ResourceLocation enderPortalEndSkyTextures = new ResourceLocation(
			"textures/environment/end_sky.png");
	private static final ResourceLocation endPortalTextures = new ResourceLocation(
			"textures/entity/end_portal.png");
	private static final Random teleporterRandom = new Random(31100L);

	FloatBuffer floatBuffer = GLAllocation.createDirectFloatBuffer(16);
	ModelTelepad padModel = new ModelTelepad();

	RenderBlocks renderBlocks;

	public RenderTileEntityTelepad() {
		renderBlocks = new RenderBlocks();
	}

	private FloatBuffer createBuffer(float par1, float par2, float par3,
			float par4) {

		this.floatBuffer.clear();
		this.floatBuffer.put(par1).put(par2).put(par3).put(par4);
		this.floatBuffer.flip();
		return this.floatBuffer;
	}

	public void renderEndPortalSurface(double par2, double par4, double par6,
			float par8) {

		float f1 = (float) TileEntityRendererDispatcher.staticPlayerX;
		float f2 = (float) TileEntityRendererDispatcher.staticPlayerY;
		float f3 = (float) TileEntityRendererDispatcher.staticPlayerZ;
		GL11.glDisable(GL11.GL_LIGHTING);
		teleporterRandom.setSeed(31100L);
		float f4 = 0.75F / 5;// offset y

		par2 += 0.05d; // offset x/z
		par6 += 0.05d; // offset x/z
		par4 += 0.04d;

		for (int i = 0; i < 16; ++i) {
			GL11.glPushMatrix();
			float f5 = 16 - i;
			float f6 = 0.0625F * 5f; // looking inside makes the stars go
			// smaller, and more numerous
			float f7 = 1.0F / ((f5 / 10) + 1.0F);// Colours the stars less or
			// more bright

			if (i == 0) {
				this.bindTexture(enderPortalEndSkyTextures);
				f7 = 0.1F;
				f5 = 65.0F;
				f6 = 0.125F;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			}

			if (i == 1) {
				this.bindTexture(endPortalTextures);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
				f6 = 0.5F;
			}

			float f8 = (float) (-(par4 + f4));
			float f9 = f8 + ActiveRenderInfo.objectY;
			float f10 = f8 + f5 + ActiveRenderInfo.objectY;
			float f11 = f9 / f10;
			f11 += (float) (par4 + f4);
			GL11.glTranslatef(f1, f11, f3);
			GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE,
					GL11.GL_OBJECT_LINEAR);
			GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE,
					GL11.GL_OBJECT_LINEAR);
			GL11.glTexGeni(GL11.GL_R, GL11.GL_TEXTURE_GEN_MODE,
					GL11.GL_OBJECT_LINEAR);
			GL11.glTexGeni(GL11.GL_Q, GL11.GL_TEXTURE_GEN_MODE,
					GL11.GL_EYE_LINEAR);
			GL11.glTexGen(GL11.GL_S, GL11.GL_OBJECT_PLANE,
					this.createBuffer(1.0F, 0.0F, 0.0F, 0.0F));
			GL11.glTexGen(GL11.GL_T, GL11.GL_OBJECT_PLANE,
					this.createBuffer(0.0F, 0.0F, 1.0F, 0.0F));
			GL11.glTexGen(GL11.GL_R, GL11.GL_OBJECT_PLANE,
					this.createBuffer(0.0F, 0.0F, 0.0F, 1.0F));
			GL11.glTexGen(GL11.GL_Q, GL11.GL_EYE_PLANE,
					this.createBuffer(0.0F, 1.0F, 0.0F, 0.0F));
			GL11.glEnable(GL11.GL_TEXTURE_GEN_S);
			GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
			GL11.glEnable(GL11.GL_TEXTURE_GEN_R);
			GL11.glEnable(GL11.GL_TEXTURE_GEN_Q);
			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_TEXTURE);
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			// GL11.glTranslatef(0.0F, (float)(Minecraft.getSystemTime() %
			// 700000L) / 700000.0F, 0.0F);
			GL11.glRotatef((Minecraft.getSystemTime() % 7000L) / 7000.0F, 1, 1,
					0);
			GL11.glScalef(f6, f6, f6);// scales stars
			GL11.glTranslatef(0.5F, 0.5f, 0.0F);
			GL11.glRotatef(((i * i * 4321) + (i * 9)) * 2.0F, 0.0F, 0.0F, 1.0F);
			GL11.glTranslatef(-0.5F, -0.5F, 0.0F);
			GL11.glTranslatef(-f1, -f3, -f2);
			f9 = f8 + ActiveRenderInfo.objectY;
			GL11.glTranslatef((ActiveRenderInfo.objectX * f5) / f9,
					(ActiveRenderInfo.objectZ * f5) / f9, -f2);
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			f11 = (teleporterRandom.nextFloat() * 0.5F) + 0.1F;
			float f12 = (teleporterRandom.nextFloat() * 0.5F) + 0.4F;
			float f13 = (teleporterRandom.nextFloat() * 0.5F) + 0.5F;

			if (i == 0) {
				f13 = 1.0F;
				f12 = 1.0F;
				f11 = 1.0F;
			}

			double scale = 0.9D;

			GL11.glTranslatef(0, 0, 0);
			tessellator.setColorRGBA_F(f11 * f7, f12 * f7, f13 * f7, 1.0F);
			tessellator.addVertex(par2, par4 + f4, par6);
			tessellator.addVertex(par2, par4 + f4, par6 + scale);
			tessellator.addVertex(par2 + scale, par4 + f4, par6 + scale);
			tessellator.addVertex(par2 + scale, par4 + f4, par6);
			tessellator.draw();
			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
		}

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_S);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_T);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_R);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_Q);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {

		animation_counter++;

		TileEntityTelepad te = null;

		if (tileentity instanceof TileEntityTelepad) {
			te = (TileEntityTelepad) tileentity;
		}

		if(te == null)
			return;

		Color colorBase = new Color(te.getColorBase());
		Color colorFrame = new Color(te.getColorFrame());

		renderPad(te, colorFrame, colorBase, x, y, z, f);

		if (te.hasDimensionUpgrade()){

			GL11.glPushMatrix();

			if (animation_counter < 100)
				bindTexture(frame_upgrade);
			else if (animation_counter < 300)
				bindTexture(frame_upgrade_2);
			else if (animation_counter < 500)
				bindTexture(frame_upgrade_3);
			else if (animation_counter < 700)
				bindTexture(frame_upgrade_4);
			else
				bindTexture(frame_upgrade_4);

			if (animation_counter == 900)
				animation_counter = 0;

			GL11.glColor3f(1, 1, 1);
			GL11.glScalef(0.75f, 0.75f, 0.75f);
			GL11.glTranslatef(-0.1f, 0.45f, 0.1f);

			switch (te.getUpgradeRotation()) {
			case 0:
				GL11.glRotatef(0f, 0, 1, 0);
				GL11.glTranslatef(0f, 0, 0f);
				break;
			case 1:
				GL11.glRotatef(-90f, 0, 1, 0);
				GL11.glTranslatef(-0.1f, 0, 0f);
				break;
			case 2:
				GL11.glRotatef(180f, 0, 1, 0);
				GL11.glTranslatef(-0.2f, 0, 0.2f);
				break;
			case 3:
				GL11.glRotatef(90f, 0, 1, 0);
				GL11.glTranslatef(0f, 0, 0.2f);
				break;
			default:
				break;
			}

			padModel.renderUpgrade(0.0625f);
			GL11.glPopMatrix();
		}

		if (te.hasRedstoneUpgrade()) {
			renderTorch(te, -0.5, 0, -0.5);
			renderTorch(te, 0.5, 0, -0.5);
			renderTorch(te, -0.5, 0, 0.5);
			renderTorch(te, 0.5, 0, 0.5);

		}

	}

	private void renderTorch(TileEntityTelepad te, double offsetX, double offsetY, double offsetZ){
		Tessellator tessellator = Tessellator.instance;

		tessellator.startDrawingQuads();
		tessellator.setTranslation(offsetX - te.xCoord - 0.5, offsetY - te.yCoord, offsetZ - te.zCoord -0.5);
		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
		renderBlocks.blockAccess = te.getWorldObj();
		this.bindTexture(TextureMap.locationBlocksTexture);

		if (te.isPowered())
			renderBlocks.renderBlockByRenderType(Blocks.redstone_torch, te.xCoord, te.yCoord, te.zCoord);
		else
			renderBlocks.renderBlockByRenderType(Blocks.unlit_redstone_torch, te.xCoord, te.yCoord, te.zCoord);

		tessellator.setTranslation(0.0D, 0.0D, 0.0D);
		tessellator.draw();
	}
	public void renderPad(TileEntity tileentity, Color colorFrame,Color colorBase, double x, double y, double z, float f) {

		TileEntityTelepad te = null;

		if (tileentity instanceof TileEntityTelepad)
			te = (TileEntityTelepad) tileentity;

		GL11.glPushMatrix();
		renderEndPortalSurface(x, y, z, f);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 2.25F,(float) z + 0.5F);
		GL11.glScalef(1.0F, -1F, -1F);

		float f2 = 1.5f;
		GL11.glScalef(f2, f2, f2);

		GL11.glPushMatrix();
		bindTexture(base);
		GL11.glColor3f((float) (colorBase.getRed() / 255.0f),(float) (colorBase.getGreen() / 255.0f),(float) (colorBase.getBlue() / 255.0f));
		padModel.renderArrows(0.0625f);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		bindTexture(pads);
		GL11.glColor3f((float) (colorFrame.getRed() / 255.0f), (float) (colorFrame.getGreen() / 255.0f), (float) (colorFrame.getBlue() / 255.0f));
		padModel.renderLegs(0.0625f);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		bindTexture(frame);
		GL11.glColor3f(1f, 1f, 1f);
		padModel.renderFrame(0.0625f);
		GL11.glPopMatrix();

		GL11.glPopMatrix();
	}
}
