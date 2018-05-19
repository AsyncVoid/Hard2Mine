package me.Async.hard2mine.block;

import java.util.Random;

import javax.annotation.Nullable;

import me.Async.hard2mine.explosion.GunpowderBarrelExplosion;
import me.Async.hard2mine.explosion.GunpowderTrailExplosion;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockGunpowderBarrel extends ModBlock {

	//public static final PropertyBool LIT = PropertyBool.create("lit");
	
	public BlockGunpowderBarrel() {
		super("gunpowder_barrel", Material.WOOD);
		//this.setDefaultState(this.blockState.getBaseState().withProperty(LIT, false));
		//this.setTickRandomly(true);
	}
	
	@Override
	public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn)
    {
        if (!worldIn.isRemote)
        {
	        explode(worldIn, pos);
        }
    }
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
	    if ((heldItem != null) && (heldItem.getItem() == Items.FLINT_AND_STEEL)) {
	        //ignite(world, pos);
	        explode(world, pos);
	        heldItem.damageItem(1, player);
	        return true;
	    }
	    return super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity)
	{
	    if (entity instanceof EntityArrow) {
	        EntityArrow entityarrow = (EntityArrow)entity;
	        if (entityarrow.isBurning()) {
	            //ignite(world, pos);
		        explode(world, pos);
	        }
	    }
	}
	
	public static void explode(World world, BlockPos pos) 
	{
		if(!world.isRemote)
		{
			GunpowderBarrelExplosion explosion = new GunpowderBarrelExplosion(world, pos);
			if (ForgeEventFactory.onExplosionStart(world, explosion))
				return;
			world.setBlockToAir(pos);
			explosion.doExplosionA();
			explosion.doExplosionB(true);
		}
	}
	
	@Override
	public boolean canDropFromExplosion(Explosion explosionIn)
    {
        return false;
    }
	
	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }
	
	@Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn)
    {
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isRemote)
        {
            this.checkFallable(worldIn, pos);
        }
    }
	
	private void checkFallable(World worldIn, BlockPos pos)
    {
        if ((worldIn.isAirBlock(pos.down()) || canFallThrough(worldIn.getBlockState(pos.down()))) && pos.getY() >= 0)
        {
            int i = 32;

            if (worldIn.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32)))
            {
                if (!worldIn.isRemote)
                {
                    EntityFallingBlock entityfallingblock = new EntityFallingBlock(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, worldIn.getBlockState(pos));
                    entityfallingblock.shouldDropItem = false;
                    //this.onStartFalling(entityfallingblock);
                    worldIn.spawnEntityInWorld(entityfallingblock);
                }
            }
            else
            {
                IBlockState state = worldIn.getBlockState(pos);
                worldIn.setBlockToAir(pos);
                BlockPos blockpos;

                for (blockpos = pos.down(); (worldIn.isAirBlock(blockpos) || canFallThrough(worldIn.getBlockState(blockpos))) && blockpos.getY() > 0; blockpos = blockpos.down())
                {
                    ;
                }

                if (blockpos.getY() > 0)
                {
                	explode(worldIn, pos);
                    //worldIn.setBlockState(blockpos.up(), state); 
                }
            }
        }
    }
	
	public static boolean canFallThrough(IBlockState state)
    {
        Block block = state.getBlock();
        Material material = state.getMaterial();
        return block == Blocks.FIRE || material == Material.AIR || material == Material.WATER || material == Material.LAVA;
    }
	
	@Override
	public int tickRate(World worldIn)
    {
        return 2;
    }
	
	/*
	
	
	private void ignite(World world, BlockPos pos)
	{
	    IBlockState state = world.getBlockState(pos);
	    if (state.getBlock() == this) {
	        //boolean lit = state.getValue(LIT).booleanValue();
	        world.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
	        		SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.9F + world.rand  //soundFizzle
	        		.nextFloat() * 0.1F);
	        world.setBlockState(pos, state.withProperty(LIT, Boolean.valueOf(true)));
	        world.scheduleUpdate(pos, this, 1);
	    }
	}
	
	
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		if (!world.isRemote)
        {
			boolean lit = state.getValue(LIT).booleanValue();
		    if (lit) {
		    	if(world.rand.nextBoolean()) {
			    	world.setBlockToAir(pos);
			    	explode(world, pos);
		    	}
			}
        }
	}
	
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)
	{
	    int l = getMetaFromState(state);
	    
	    if (l > 0) {
	        double d0 = pos.getX() + 0.5D + (rand.nextFloat() - 0.5D) * 0.5D;
	        double d1 = pos.getY() + 1F;
	        double d2 = pos.getZ() + 0.5D + (rand.nextFloat() - 0.5D) * 0.5D;
	        float f = l / 15.0F;
	        float velY = f * 0.03F;
	        float velX = rand.nextFloat() * 0.02F - 0.01F;
	        float velZ = rand.nextFloat() * 0.02F - 0.01F;
	      
	        world.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, velX, velY, velZ, new int[0]);
	        world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d0, d1, d2, velX, velY, velZ, new int[0]);
	    }
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(LIT, Boolean.valueOf(meta == 1 ? true : false));
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
	    return state.getValue(LIT).booleanValue() ? 1 : 0;
	}
	
	@Override
	protected BlockStateContainer createBlockState()
	{
	    return new BlockStateContainer(this, new IProperty[] { LIT });
	}*/
}
