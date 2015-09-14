package net.subaraki.telepads.blocks;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.subaraki.telepads.Telepads;
import net.subaraki.telepads.tileentity.TileEntityTelepad;
import net.subaraki.telepads.util.Constants;

public class BlockTelepad extends BlockContainer {
    
    public BlockTelepad(Material mat) {
        super(mat);
        
        float f = 0.5F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
    }
    
    @Override
    public TileEntity createNewTileEntity (World world, int meta) {
        
        return new TileEntityTelepad();
    }
    
    @Override
    public void breakBlock (World par1World, int par2, int par3, int par4, Block b, int par6) {
        
        super.breakBlock(par1World, par2, par3, par4, b, par6);
        par1World.removeTileEntity(par2, par3, par4);
    }
    
    @Override
    public boolean canEntityDestroy (IBlockAccess world, int x, int y, int z, Entity entity) {
        
        return false;
    }
    
    @Override
    protected void dropBlockAsItem (World world, int x, int y, int z, ItemStack is) {
        
        super.dropBlockAsItem(world, x, y, z, is);
    }
    
    @Override
    public int getRenderType () {
        
        return RenderingRegistry.getNextAvailableRenderId();
    }
    
    @Override
    public boolean isOpaqueCube () {
        
        return false;
    }
    
    @Override
    public void onBlockPlacedBy (World par1World, int x, int y, int z, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {
        
        if (par1World.provider.dimensionId == 1) {
            par1World.newExplosion((Entity) null, x + 0.5F, y + 0.5F, z + 0.5F, 5.0F, true, true);
            par1World.setBlockToAir(x, y, z);
            par1World.removeTileEntity(x, y, z);
            if (!par1World.isRemote) // this should not crash. only player can
                                     // put this down
                ((EntityPlayer) par5EntityLivingBase).addChatMessage(new ChatComponentText("The Magic in the End was too strong for the TelePad..."));
                
            return;
        }
        
        TileEntityTelepad te = new TileEntityTelepad();
        
        if (par5EntityLivingBase instanceof EntityPlayer) {
            EntityPlayer p = (EntityPlayer) par5EntityLivingBase;
            
            te.ownerName = p.getDisplayName();
            te.dimension = par1World.provider.dimensionId;
            
            p.openGui(Telepads.instance, Constants.GUI_ID_NAMEPAD, par1World, x, y, z);
        }
        
        par1World.markBlockForUpdate(te.xCoord, te.yCoord, te.zCoord);
        par1World.setTileEntity(x, y, z, te);
    }
}
