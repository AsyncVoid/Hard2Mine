package me.Async.hard2mine.block;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import me.Async.hard2mine.registry.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockGunpowderTrail extends BlockHardBase {
	public static final PropertyEnum<BlockGunpowderTrail.EnumAttachPosition> NORTH = PropertyEnum.<BlockGunpowderTrail.EnumAttachPosition>create("north", BlockGunpowderTrail.EnumAttachPosition.class);
    public static final PropertyEnum<BlockGunpowderTrail.EnumAttachPosition> EAST = PropertyEnum.<BlockGunpowderTrail.EnumAttachPosition>create("east", BlockGunpowderTrail.EnumAttachPosition.class);
    public static final PropertyEnum<BlockGunpowderTrail.EnumAttachPosition> SOUTH = PropertyEnum.<BlockGunpowderTrail.EnumAttachPosition>create("south", BlockGunpowderTrail.EnumAttachPosition.class);
    public static final PropertyEnum<BlockGunpowderTrail.EnumAttachPosition> WEST = PropertyEnum.<BlockGunpowderTrail.EnumAttachPosition>create("west", BlockGunpowderTrail.EnumAttachPosition.class);
	
	public BlockGunpowderTrail() {
		super("gunpowder_trail", Material.TNT);
		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(NORTH, BlockGunpowderTrail.EnumAttachPosition.NONE)
				.withProperty(EAST, BlockGunpowderTrail.EnumAttachPosition.NONE)
				.withProperty(SOUTH, BlockGunpowderTrail.EnumAttachPosition.NONE)
				.withProperty(WEST, BlockGunpowderTrail.EnumAttachPosition.NONE));
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }
	
	@Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }
	
	@Override
	public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState();
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }
    
    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        switch (rot)
        {
            case CLOCKWISE_180:
                return state.withProperty(NORTH, state.getValue(SOUTH)).withProperty(EAST, state.getValue(WEST)).withProperty(SOUTH, state.getValue(NORTH)).withProperty(WEST, state.getValue(EAST));
            case COUNTERCLOCKWISE_90:
                return state.withProperty(NORTH, state.getValue(EAST)).withProperty(EAST, state.getValue(SOUTH)).withProperty(SOUTH, state.getValue(WEST)).withProperty(WEST, state.getValue(NORTH));
            case CLOCKWISE_90:
                return state.withProperty(NORTH, state.getValue(WEST)).withProperty(EAST, state.getValue(NORTH)).withProperty(SOUTH, state.getValue(EAST)).withProperty(WEST, state.getValue(SOUTH));
            default:
                return state;
        }
    }
    
    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        switch (mirrorIn)
        {
            case LEFT_RIGHT:
                return state.withProperty(NORTH, state.getValue(SOUTH)).withProperty(SOUTH, state.getValue(NORTH));
            case FRONT_BACK:
                return state.withProperty(EAST, state.getValue(WEST)).withProperty(WEST, state.getValue(EAST));
            default:
                return super.withMirror(state, mirrorIn);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public static int colorMultiplier(int p_176337_0_)
    {
    	return -14671840; //4280295456 0xFF202020
    }
    
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return new AxisAlignedBB(0, 0, 0, 1, 0.0625d, 1);
    }
    
    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }
    
    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, getProperties());
    }
    
    private static IProperty[] getProperties()
    {
    	return new IProperty[] {NORTH, EAST, SOUTH, WEST};
    }
    
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        state = state.withProperty(WEST, this.getAttachPosition(worldIn, pos, EnumFacing.WEST));
        state = state.withProperty(EAST, this.getAttachPosition(worldIn, pos, EnumFacing.EAST));
        state = state.withProperty(NORTH, this.getAttachPosition(worldIn, pos, EnumFacing.NORTH));
        state = state.withProperty(SOUTH, this.getAttachPosition(worldIn, pos, EnumFacing.SOUTH));
        return state;
    }
	
	private BlockGunpowderTrail.EnumAttachPosition getAttachPosition(IBlockAccess worldIn, BlockPos pos, EnumFacing direction)
    {
        BlockPos blockpos = pos.offset(direction);
        IBlockState iblockstate = worldIn.getBlockState(pos.offset(direction));

        if (!canConnectTo(worldIn.getBlockState(blockpos), direction, worldIn, blockpos) && (iblockstate.isNormalCube() || !canConnectUpwardsTo(worldIn, blockpos.down())))
        {
            IBlockState iblockstate1 = worldIn.getBlockState(pos.up());

            if (!iblockstate1.isNormalCube())
            {
                boolean flag = worldIn.getBlockState(blockpos).isSideSolid(worldIn, blockpos, EnumFacing.UP) || worldIn.getBlockState(blockpos).getBlock() == Blocks.GLOWSTONE;

                if (flag && canConnectUpwardsTo(worldIn, blockpos.up()))
                {
                    if (iblockstate.isBlockNormalCube())
                    {
                        return BlockGunpowderTrail.EnumAttachPosition.UP;
                    }

                    return BlockGunpowderTrail.EnumAttachPosition.SIDE;
                }
            }

            return BlockGunpowderTrail.EnumAttachPosition.NONE;
        }
        else
        {
            return BlockGunpowderTrail.EnumAttachPosition.SIDE;
        }
    }
	
	protected static boolean canConnectUpwardsTo(IBlockAccess worldIn, BlockPos pos)
    {
        return canConnectTo(worldIn.getBlockState(pos), null, worldIn, pos);
    }

    protected static boolean canConnectTo(IBlockState blockState, @Nullable EnumFacing side, IBlockAccess world, BlockPos pos )
    {
        return blockState.getBlock() == BlockRegistry.gunpowder_trail;
    }
	
	public void ignite(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote && state.getBlock() == BlockRegistry.gunpowder_trail)
        {
        	IBlockState actualState = state.getActualState(worldIn, pos);
        	
        	List<BlockPos> surrounding = new ArrayList<BlockPos>();
        	if(actualState.getValue(NORTH) == EnumAttachPosition.UP)
            {
        		surrounding.add(pos.offset(EnumFacing.NORTH).offset(EnumFacing.UP));
            }
        	if(actualState.getValue(NORTH) == EnumAttachPosition.SIDE)
            {
        		surrounding.add(pos.offset(EnumFacing.NORTH).offset(EnumFacing.DOWN));
            }
            if(actualState.getValue(EAST) == EnumAttachPosition.SIDE)
            {
            	surrounding.add(pos.offset(EnumFacing.EAST).offset(EnumFacing.DOWN));
            }
            if(actualState.getValue(EAST) == EnumAttachPosition.UP)
            {
        		surrounding.add(pos.offset(EnumFacing.EAST).offset(EnumFacing.UP));
            }
            if(actualState.getValue(SOUTH) == EnumAttachPosition.SIDE)
            {
            	surrounding.add(pos.offset(EnumFacing.SOUTH).offset(EnumFacing.DOWN));
            }
            if(actualState.getValue(SOUTH) == EnumAttachPosition.UP)
            {
        		surrounding.add(pos.offset(EnumFacing.SOUTH).offset(EnumFacing.UP));
            }
            if(actualState.getValue(WEST) == EnumAttachPosition.SIDE)
            {
            	surrounding.add(pos.offset(EnumFacing.WEST).offset(EnumFacing.DOWN));
            }
            if(actualState.getValue(WEST) == EnumAttachPosition.UP)
            {
        		surrounding.add(pos.offset(EnumFacing.WEST).offset(EnumFacing.UP));
            }
        	
            worldIn.playSound((EntityPlayer)null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX(), pos.getY(), pos.getZ(), 0.0f, 0.5f, 0.0f);
            worldIn.setBlockState(pos, Blocks.FIRE.getDefaultState(), 11);
            
            for(BlockPos position : surrounding)
            {
            	ignite(worldIn, position, worldIn.getBlockState(position));
            }
        }
    }
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (heldItem != null && (heldItem.getItem() == Items.FLINT_AND_STEEL || heldItem.getItem() == Items.FIRE_CHARGE))
        {
            this.ignite(worldIn, pos, state);
            //worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);

            if (heldItem.getItem() == Items.FLINT_AND_STEEL)
            {
                heldItem.damageItem(1, playerIn);
            }
            else if (!playerIn.capabilities.isCreativeMode)
            {
                --heldItem.stackSize;
            }

            return true;
        }
        else
        {
            return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
        }
    }
	
	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        if (!worldIn.isRemote && entityIn instanceof EntityArrow)
        {
            EntityArrow entityarrow = (EntityArrow)entityIn;

            if (entityarrow.isBurning())
            {
                this.ignite(worldIn, pos, worldIn.getBlockState(pos));
                //worldIn.setBlockToAir(pos);
            }
        }
    }
	
	@Override
	public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn)
    {
        ignite(worldIn, pos, worldIn.getBlockState(pos));
    }
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn)
    {
		for(EnumFacing facing : EnumFacing.VALUES)
        {
        	if(worldIn.getBlockState(pos.offset(facing)).getBlock() == Blocks.FIRE)
        	{
        		this.ignite(worldIn, pos, state);
        		break;
        	}
        }
		
		//IBlockState actualState = state.getActualState(worldIn, pos);
		/*
        if(actualState.getValue(NORTH) == EnumAttachPosition.UP)
        {
        	BlockPos pos2 = pos.offset(EnumFacing.NORTH).offset(EnumFacing.UP);
        	this.ignite(worldIn, pos2, state);
        }
        if(actualState.getValue(EAST) == EnumAttachPosition.UP)
        {
        	BlockPos pos2 = pos.offset(EnumFacing.EAST).offset(EnumFacing.UP);
        	this.ignite(worldIn, pos2, state);
        }
        if(actualState.getValue(SOUTH) == EnumAttachPosition.UP)
        {
        	BlockPos pos2 = pos.offset(EnumFacing.SOUTH).offset(EnumFacing.UP);
        	this.ignite(worldIn, pos2, state);
        }
        if(actualState.getValue(WEST) == EnumAttachPosition.UP)
        {
        	BlockPos pos2 = pos.offset(EnumFacing.WEST).offset(EnumFacing.UP);
        	this.ignite(worldIn, pos2, state);
        }*/
        
        
    }

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        return 100;
    }
	
	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        return 200;
    }
	
	static enum EnumAttachPosition implements IStringSerializable
    {
        UP("up"),
        SIDE("side"),
        NONE("none");

        private final String name;

        private EnumAttachPosition(String name)
        {
            this.name = name;
        }

        public String toString()
        {
            return this.getName();
        }

        public String getName()
        {
            return this.name;
        }
    }
	
}
