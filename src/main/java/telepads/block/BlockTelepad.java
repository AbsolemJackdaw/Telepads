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

		/**===================If the player clicking the pad IS the owner=================*/

		if(te.ownerName.equals(par5EntityPlayer.getDisplayName())){
			if(!par5EntityPlayer.isSneaking()){
				if(!te.lockedUniversal){
					if(par5EntityPlayer.getCurrentEquippedItem() != null)
						if(par5EntityPlayer.getCurrentEquippedItem().getItem()== Telepads.crowbar)	{

							par1World.setBlockToAir(x, y, z);
							par1World.removeTileEntity(x, y, z);

							ItemStack stack = new ItemStack(Telepads.telepad);
							EntityItem ei = new EntityItem(par1World, x, y, z, stack);
							if(!par1World.isRemote) {
								par1World.spawnEntityInWorld(ei);
							}

							PlayerPadData.get(par5EntityPlayer).removePad(te);
							//TODO is there a reason for this double removal ?
							removePadFromRegister(par5EntityPlayer, te);
						}
				} else if(par1World.isRemote) {
					par5EntityPlayer.addChatComponentMessage(
							new ChatComponentText("This Universal Pad got locked and" +
									" can not be removed because other players registered to it."));
				}

			}
			//sneaking
			else{
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
					if(!par1World.isRemote) {
						par5EntityPlayer.addChatComponentMessage(
								new ChatComponentText("Your portal got Closed"));
					}
					te.isUniversal = false;
					par1World.markBlockForUpdate(te.xCoord, te.yCoord, te.zCoord);
				}
			}

		}

		/**===================If the player clicking the pad is NOT the owner=================*/
		else{
			//If the player clicking the pad is not the owner
			if(!te.isUniversal) {
				if(!par1World.isRemote)
					par5EntityPlayer.addChatMessage(new ChatComponentText("This is not my TelePad..."));
				return true;
			}

			if(par5EntityPlayer.getCurrentEquippedItem() != null){

				PlayerPadData data = PlayerPadData.get(par5EntityPlayer);

				int[] a = new int[3]; a[0] = te.xCoord; a[1] = te.yCoord; a[2] = te.zCoord;

				for(int[] coords : data.getAllCoords()){
					if((coords[0] == a[0]) && (coords[1] == a[1]) && (coords[2] == a[2])){
						if(!par1World.isRemote) {
							par5EntityPlayer.addChatComponentMessage(
									new ChatComponentText("I already registered this pad"));
						}
						return true;
					}
				}
				data.getAllCoords().add(a);
				data.getAllNames().add(te.telepadname);
				data.getAllDims().add(te.dimension);

				if(!te.lockedUniversal) {
					te.lockedUniversal = true;
				}
				if(!par1World.isRemote) {
					par5EntityPlayer.addChatComponentMessage(
							new ChatComponentText("Registered Universal Pad to Register"));
				}
			}
		}
		return true;
	}

	@Override
	public void onBlockPlacedBy(World par1World, int x, int y, int z,
			EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {


		if(par1World.provider.dimensionId == 1){
			par1World.newExplosion((Entity)null, x + 0.5F, y + 0.5F, z + 0.5F, 5.0F, true, true);
			par1World.setBlockToAir(x, y, z);
			par1World.removeTileEntity(x, y, z);
			if(!par1World.isRemote) //this should not crash. only player can put this down
				((EntityPlayer) par5EntityLivingBase).addChatMessage(new ChatComponentText("The Magic in the End was too strong for the TelePad..."));

			return;
		}

		TETelepad te = new TETelepad();

		if(par5EntityLivingBase instanceof EntityPlayer){
			EntityPlayer p = (EntityPlayer)par5EntityLivingBase;

			te.ownerName = p.getDisplayName();
			te.dimension = par1World.provider.dimensionId;

			p.openGui(Telepads.instance, TelePadGuiHandler.NAMETELEPAD, par1World, x, y, z);
		}
		par1World.markBlockForUpdate(te.xCoord, te.yCoord, te.zCoord);
		par1World.setTileEntity(x, y, z, te);
	}



	@Override
	public void randomDisplayTick(World par1World, int x, int y, int z, Random par5Random)
	{
		TETelepad te = (TETelepad)par1World.getTileEntity(x, y, z);

		if(te == null)
			return;

		if(te.isStandingOnPlatform) {
			for (int l = 0; l < 100; ++l)
			{
				double d1 = y + (par5Random.nextFloat()*1.5f);
				double d0 = x + par5Random.nextFloat();
				double d2 = 0.0D;
				double d3 = 0.0D;
				double d4 = 0.0D;
				int i1 = (par5Random.nextInt(2) * 2) - 1;
				int j1 = (par5Random.nextInt(2) * 2) - 1;
				d2 = (par5Random.nextFloat() - 0.5D) * 0.125D;
				d3 = (par5Random.nextFloat() - 0.5D) * 0.125D;
				d4 = (par5Random.nextFloat() - 0.5D) * 0.125D;
				d4 = par5Random.nextFloat() * 1.0F * j1;
				d2 = par5Random.nextFloat() * 1.0F * i1;
				par1World.spawnParticle("portal", x+0.5, d1, z+0.5, d2, d3, d4);
			}
		} else{
			for (int l = 0; l < 5; ++l)
			{
				double d1 = y + (par5Random.nextFloat()*1.5f);
				double d0 = z + par5Random.nextFloat();
				double d2 = 0.0D;
				double d3 = 0.0D;
				double d4 = 0.0D;
				int i1 = (par5Random.nextInt(2) * 2) - 1;
				int j1 = (par5Random.nextInt(2) * 2) - 1;
				d2 = (par5Random.nextFloat() - 0.5D) * 0.125D;
				d3 = (par5Random.nextFloat() - 0.5D) * 0.125D;
				d4 = (par5Random.nextFloat() - 0.5D) * 0.125D;
				d4 = par5Random.nextFloat() * 1.0F * j1;
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

	@Override
	public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
		return getExplosionResistance(par1Entity);
	}

	@Override
	public float getExplosionResistance(Entity par1Entity) {
		return 18000000F;
	}
}
