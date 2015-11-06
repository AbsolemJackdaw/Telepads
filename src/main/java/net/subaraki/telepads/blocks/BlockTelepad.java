package net.subaraki.telepads.blocks;

import java.util.Random;

import net.darkhax.bookshelf.lib.Position;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.subaraki.telepads.Telepads;
import net.subaraki.telepads.common.network.PacketSyncPoweredBlock;
import net.subaraki.telepads.handler.PlayerLocations;
import net.subaraki.telepads.handler.PlayerLocations.TelepadEntry;
import net.subaraki.telepads.tileentity.TileEntityTelepad;
import net.subaraki.telepads.util.Constants;

public class BlockTelepad extends BlockContainer {
    
    public BlockTelepad() {
        
        super(Material.glass);
        
        this.setBlockName("telepad");
        this.setLightLevel(0.2f);
        this.setHardness(5f);
        this.setBlockTextureName("glass");
        this.setStepSound(soundTypeGlass);
        this.setCreativeTab(CreativeTabs.tabTransport);
        this.setHarvestLevel("pickaxe", 1, 0);
        
        float offset = 0.5F;
        this.setBlockBounds(0.5F - offset, 0.0F, 0.5F - offset, 0.5F + offset, 0.25F, 0.5F + offset);
        
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
        
        if (entity instanceof EntityPlayer)
            return true;
            
        return false;
    }
    
    @Override
    protected void dropBlockAsItem (World world, int x, int y, int z, ItemStack is) {
        
        // keep empty
    }
    
    @Override
    public int quantityDropped (int meta, int fortune, Random random) {
        
        return 0;
    }
    
    @Override
    public int getRenderType () {
        
        return -1;
    }
    
    @Override
    public boolean isOpaqueCube () {
        
        return false;
    }
    
    @Override
    public void onBlockPlacedBy (World par1World, int x, int y, int z, EntityLivingBase elb, ItemStack is) {
        
        TileEntityTelepad te = new TileEntityTelepad();
        
        if (elb instanceof EntityPlayer) {
            EntityPlayer p = (EntityPlayer) elb;
            
            te.setDimension(par1World.provider.dimensionId);
            
            p.openGui(Telepads.instance, Constants.GUI_ID_NAMEPAD, par1World, x, y, z);
        }
        
        if (is.hasTagCompound()) {
            if (is.getTagCompound().hasKey("colorFrame"))
                te.setFrameColor(is.getTagCompound().getInteger("colorFrame"));
            if (is.getTagCompound().hasKey("colorBase"))
                te.setBaseColor(is.getTagCompound().getInteger("colorBase"));
        }
        
        par1World.markBlockForUpdate(te.xCoord, te.yCoord, te.zCoord);
        par1World.setTileEntity(x, y, z, te);
        
    }
    
    @Override
    public boolean onBlockActivated (World w, int x, int y, int z, EntityPlayer player, int meta, float f, float f1, float f2) {
        
        if (player.getHeldItem() == null) {
            if (player.isSneaking() && w.getTileEntity(x, y, z) instanceof TileEntityTelepad) {
                
                TileEntityTelepad telepad = (TileEntityTelepad) w.getTileEntity(x, y, z);
                PlayerLocations pl = PlayerLocations.getProperties(player);
                
                boolean match = false;
                
                for (TelepadEntry tpe : pl.getEntries()) {
                    
                    if (tpe.position.getX() == x)
                        if (tpe.position.getY() == y)
                            if (tpe.position.getZ() == z)
                                match = true;
                }
                
                if (!match) {
                    pl.addEntry(new TelepadEntry(telepad.getTelePadName(), telepad.getDimension(), new Position(x, y, z), false, false));
                    if (!w.isRemote)
                        player.addChatMessage(new ChatComponentText("Succesfully added " + telepad.getTelePadName()));
                }
                else if (!w.isRemote)
                    player.addChatMessage(new ChatComponentText(telepad.getTelePadName() + " has already been registered"));
                    
                pl.sync();
            }
        }
        
        return false;
    }
    
    @Override
    public float getExplosionResistance (Entity ent) {
        
        return Float.MAX_VALUE;
    }
    
    @Override
    public boolean removedByPlayer (World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        
        if (getTileEntityTelepad(world, x, y, z)) {
            TileEntityTelepad telepad = (TileEntityTelepad) world.getTileEntity(x, y, z);
            
            PlayerLocations pl = PlayerLocations.getProperties(player);
            
            for (TelepadEntry tpe : pl.getEntries()) {
                if (tpe.position.equals(new Position(telepad.xCoord, telepad.yCoord, telepad.zCoord)))
                    if (tpe.dimensionID == telepad.getWorldObj().provider.dimensionId) {
                        if (tpe.entryName.equals(telepad.getTelePadName())) {
                            
                            pl.removeEntry(tpe);
                            dropPad(world, telepad, x, y, z);
                            Telepads.printDebugMessage("pad was removed succesfully");
                            if (telepad.hasDimensionUpgrade())
                                world.spawnEntityInWorld(new EntityItem(world, x, y, z, new ItemStack(Telepads.transmitter, 1)));
                                
                            return world.setBlockToAir(x, y, z);
                            
                        }
                    }
            }
        }
        return false;
    }
    
    private void dropPad (World world, TileEntityTelepad telepad, int x, int y, int z) {
        
        EntityItem ei = new EntityItem(world);
        ei.setPosition(x, y, z);
        
        ItemStack stack = new ItemStack(Telepads.blockPad);
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("colorBase", telepad.getColorBase());
        nbt.setInteger("colorFrame", telepad.getColorFrame());
        stack.setTagCompound(nbt);
        
        ei.setEntityItemStack(stack);
        
        world.spawnEntityInWorld(ei);
    }
    
    @Override
    public void onNeighborBlockChange (World world, int x, int y, int z, Block neighborBlock) {
        
        if (getTileEntityTelepad(world, x, y, z)) {
            
            TileEntityTelepad telepad = (TileEntityTelepad) world.getTileEntity(x, y, z);
            
            if (telepad.hasRedstoneUpgrade() && !world.isRemote) {
                boolean flag = false;
                
                if (world.getBlockPowerInput(x, y, z) > 0)
                    flag = true;
                    
                for (int x2 = x - 1; x2 < x + 2; x2++)
                    for (int z2 = z - 1; z2 < z + 2; z2++) {
                        if (world.getBlock(x2, y, z2).canProvidePower()) {
                            int meta = world.getBlockMetadata(x2, y, z2);
                            // bug where placing two redstone block next to the pad by removing
                            // the one right next to it first, then the diagonal will keep the
                            // pad powered
                            // redstone block under pad will not activate pad
                            if (world.getBlock(x2, y, z2).isProvidingStrongPower(world, x2, y, z2, meta) > 0 || world.getBlock(x2, y, z2).isProvidingWeakPower(world, x2, y, z2, meta) > 0) {
                                flag = true;
                            }
                        }
                    }
                    
                telepad.setPowered(flag);
                Telepads.instance.network.sendToAll(new PacketSyncPoweredBlock(flag, new Position(x, y, z)));
                
                telepad.markDirty();
                
                if (!world.isRemote) {
                    for (Integer i : DimensionManager.getStaticDimensionIDs()) {
                        WorldServer ws = MinecraftServer.getServer().worldServerForDimension(i);
                        
                        for (Object o : ws.playerEntities) {
                            if (o instanceof EntityPlayer) {
                                EntityPlayer player = (EntityPlayer) o;
                                PlayerLocations pl = PlayerLocations.getProperties(player);
                                
                                for (TelepadEntry tpe : pl.getEntries()) {
                                    if (tpe.position.equals(new Position(x, y, z)))
                                        if (tpe.dimensionID == world.provider.dimensionId)
                                            tpe.setPowered(flag);
                                }
                                pl.sync();
                            }
                        }
                    }
                }
            }
        }
    }
    
    private boolean getTileEntityTelepad (World world, int x, int y, int z) {
        
        TileEntity te = world.getTileEntity(x, y, z);
        TileEntityTelepad telepad = null;
        
        if (te == null)
            return false;
            
        if (te instanceof TileEntityTelepad)
            telepad = (TileEntityTelepad) te;
            
        if (telepad == null)
            return false;
            
        return true;
    }
}
