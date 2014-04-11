package telepads.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import telepads.Telepads;
import telepads.util.PlayerPadData;
import telepads.util.TelePadGuiHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class BlockTelepad extends BlockContainer{

	public BlockTelepad(Material par2Material) {
		super( par2Material);
		float f = 0.5F;
		this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
	}

	@Override
	public void breakBlock(World par1World, int par2, int par3, int par4,Block b, int par6) {
		super.breakBlock(par1World, par2, par3, par4, b, par6);

		par1World.removeTileEntity(par2, par3, par4);
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z,
			Entity entity) {
		return false;
	}


	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TETelepad();
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TETelepad();
	}

	@Override
	protected void dropBlockAsItem(World p_149642_1_, int p_149642_2_,int p_149642_3_, int p_149642_4_, ItemStack p_149642_5_) {
		super.dropBlockAsItem(p_149642_1_, p_149642_2_, p_149642_3_, p_149642_4_,p_149642_5_);
	}

	@Override
	public int getRenderType(){
		return RenderingRegistry.getNextAvailableRenderId();
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean onBlockActivated(World par1World, int x, int y,
			int z, EntityPlayer par5EntityPlayer, int par6, float par7,
			float par8, float par9) {

		TETelepad te = (TETelepad)par1World.getTileEntity(x, y, z);

		if(!par5EntityPlayer.isSneaking()){
			if(!te.lockedUniversal){
				if(par5EntityPlayer.inventory.hasItem(Telepads.register)){
					if(te.ownerName.equals(par5EntityPlayer.getDisplayName())){
						par1World.setBlockToAir(x, y, z);
						par1World.removeTileEntity(x, y, z);

						ItemStack stack = new ItemStack(Telepads.telepad);
						EntityItem ei = new EntityItem(par1World, x, y, z, stack);
						if(!par1World.isRemote)
							par1World.spawnEntityInWorld(ei);

						PlayerPadData.get(par5EntityPlayer).removePad(te);

						removePadFromRegister(par5EntityPlayer, te);
					}else
						if(par1World.isRemote)
							par5EntityPlayer.addChatComponentMessage(new ChatComponentText("This is not mine. I should not do this !"));
				}else
					if(par1World.isRemote)
						par5EntityPlayer.addChatComponentMessage(new ChatComponentText("I havent got my register with me ... "));
			}else{
				if(par1World.isRemote)
					par5EntityPlayer.addChatComponentMessage(new ChatComponentText("This Universal Pad got locked because other players registered to it."));
			}
		} else if(te.ownerName.equals(par5EntityPlayer.getDisplayName())){
			if(!te.isUniversal){
				if(!par1World.isRemote){
					par5EntityPlayer.addChatComponentMessage(
							new ChatComponentText("Your portal got opened to Universal Acces"));
					par5EntityPlayer.addChatComponentMessage(
							new ChatComponentText("Your Friends can now add your pad to their coordinates."));
					par5EntityPlayer.addChatComponentMessage(
							new ChatComponentText("Pad will be locked to Universal if other players register"));
				}
				te.isUniversal = true;
				par1World.markBlockForUpdate(te.xCoord, te.yCoord, te.zCoord);

			}else{
				if(!par1World.isRemote)
					par5EntityPlayer.addChatComponentMessage(
							new ChatComponentText("Your portal got Closed"));
				te.isUniversal = false;
				par1World.markBlockForUpdate(te.xCoord, te.yCoord, te.zCoord);
			}

		}else{
			//If the player clicking the pad is not the owner
			if(!te.isUniversal)return false;

			if(!par5EntityPlayer.inventory.hasItem(Telepads.register)){
				if(!par1World.isRemote){
					par5EntityPlayer.addChatComponentMessage(
							new ChatComponentText("I need my register for this ... "));
					return false;
				}

				if(par5EntityPlayer.getCurrentEquippedItem() != null)
					if(!par5EntityPlayer.getCurrentEquippedItem().equals(Telepads.register)){
						if(!par1World.isRemote){
							par5EntityPlayer.addChatComponentMessage(
									new ChatComponentText("I need to use my register on the pad !"));
							return false;
						}
					} else
						for(int i = 0;i < par5EntityPlayer.inventory.mainInventory.length;i ++){
							if(par5EntityPlayer.inventory.getStackInSlot(i) != null){
								ItemStack is = par5EntityPlayer.inventory.getStackInSlot(i);

								if(is.getItem().equals(Telepads.register)){
									int[] a = new int[3]; a[0] = te.xCoord; a[1] = te.yCoord; a[2] = te.zCoord;

									PlayerPadData.get(par5EntityPlayer).getAllCoords().add(a);
									PlayerPadData.get(par5EntityPlayer).getAllNames().add(te.telepadname);
									PlayerPadData.get(par5EntityPlayer).getAllDims().add(te.dimension);
									if(!te.lockedUniversal)
										te.lockedUniversal = true;
								}
							}
						}
			}
		}
		return false;
	}

	@Override
	public void onBlockPlacedBy(World par1World, int x, int y, int z,
			EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {


		if(par1World.provider.dimensionId == 1){
			par1World.newExplosion((Entity)null, (double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), 5.0F, true, true);
			par1World.setBlockToAir(x, y, z);
			par1World.removeTileEntity(x, y, z);
			return;
		}
		
		TETelepad te = new TETelepad();

		int c = 0;
		if(par5EntityLivingBase instanceof EntityPlayer){
			EntityPlayer p = (EntityPlayer)par5EntityLivingBase;

			te.ownerName = p.getDisplayName();
			te.dimension = par1World.provider.dimensionId;

			p.openGui(Telepads.instance, TelePadGuiHandler.NAMETELEPAD, par1World, x, y, z);
		}
		par1World.setTileEntity(x, y, z, te);
	}


	@Override
	public void onEntityCollidedWithBlock(World par1World, int x, int y, int z, Entity par5Entity)
	{
		if(par5Entity instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer)par5Entity;
			TETelepad te = (TETelepad)par1World.getTileEntity(x, y, z);

			te.playerStandingOnPad = player;
		}
	}

	@Override
	public void randomDisplayTick(World par1World, int x, int y, int z, Random par5Random)
	{
		TETelepad te = (TETelepad)par1World.getTileEntity(x, y, z);

		if(te.isStandingOnPlatform)
			//	par1World.playSound((double)x , (double)y, (double)z, "subaraki:telepadLong", 0.7F, par5Random.nextFloat() * 0.4F + 0.4F, false);
			for (int l = 0; l < 100; ++l)
			{
				double d0 = x + par5Random.nextFloat();
				double d1 = y + (par5Random.nextFloat()*1.5f);
				d0 = z + par5Random.nextFloat();
				double d2 = 0.0D;
				double d3 = 0.0D;
				double d4 = 0.0D;
				int i1 = (par5Random.nextInt(2) * 2) - 1;
				int j1 = (par5Random.nextInt(2) * 2) - 1;
				d2 = (par5Random.nextFloat() - 0.5D) * 0.125D;
				d3 = (par5Random.nextFloat() - 0.5D) * 0.125D;
				d4 = (par5Random.nextFloat() - 0.5D) * 0.125D;
				double d5 = z + 0.5D + (0.25D * j1);
				d4 = par5Random.nextFloat() * 1.0F * j1;
				double d6 = x + 0.5D + (0.25D * i1);
				d2 = par5Random.nextFloat() * 1.0F * i1;
				par1World.spawnParticle("portal", x+0.5, d1, z+0.5, d2, d3, d4);
			}
		else{
			if (par5Random.nextInt(50) == 0)
			{
				//par1World.playSound((double)x , (double)y, (double)z, "subaraki:telepadShort", 1.0F, par5Random.nextFloat() * 0.4F + 0.8F, false);
			}
			for (int l = 0; l < 5; ++l)
			{
				double d0 = x + par5Random.nextFloat();
				double d1 = y + (par5Random.nextFloat()*1.5f);
				d0 = z + par5Random.nextFloat();
				double d2 = 0.0D;
				double d3 = 0.0D;
				double d4 = 0.0D;
				int i1 = (par5Random.nextInt(2) * 2) - 1;
				int j1 = (par5Random.nextInt(2) * 2) - 1;
				d2 = (par5Random.nextFloat() - 0.5D) * 0.125D;
				d3 = (par5Random.nextFloat() - 0.5D) * 0.125D;
				d4 = (par5Random.nextFloat() - 0.5D) * 0.125D;
				double d5 = z + 0.5D + (0.25D * j1);
				d4 = par5Random.nextFloat() * 1.0F * j1;
				double d6 = x + 0.5D + (0.25D * i1);
				d2 = par5Random.nextFloat() * 1.0F * i1;
				par1World.spawnParticle("portal", x+0.5, d1, z+0.5, d2, d3, d4);
			}
		}
	}

	private void removePadFromRegister(EntityPlayer p, TETelepad pad) {

		if(!p.worldObj.isRemote)
			PlayerPadData.get(p).removePad(pad);
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
}
