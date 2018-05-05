package me.Async.hard2mine.block;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import me.Async.hard2mine.explosion.GunpowderTrailExplosion;
import me.Async.hard2mine.registry.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockGunpowderTrail extends BlockHardBase implements IBlockColor {
	public static final PropertyEnum NORTH = PropertyEnum.<EnumAttachPosition>create("north", EnumAttachPosition.class);
    public static final PropertyEnum EAST = PropertyEnum.<EnumAttachPosition>create("east", EnumAttachPosition.class);
	public static final PropertyEnum SOUTH = PropertyEnum.<EnumAttachPosition>create("south", EnumAttachPosition.class);
	public static final PropertyEnum WEST = PropertyEnum.<EnumAttachPosition>create("west", EnumAttachPosition.class);
	public static final PropertyInteger BURNING = PropertyInteger.create("burning", 0, 8);
	protected static final AxisAlignedBB[] GUNPOWDER_AABB = { new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 0.8125D), new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 0.8125D), new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 1.0D), new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 0.8125D, 0.0625D, 0.8125D), new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 0.8125D, 0.0625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.8125D, 0.0625D, 0.8125D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.8125D, 0.0625D, 1.0D), new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 1.0D, 0.0625D, 0.8125D), new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 1.0D, 0.0625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 1.0D, 0.0625D, 0.8125D), new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 1.0D, 0.0625D, 1.0D), new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 1.0D, 0.0625D, 0.8125D), new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 0.8125D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D) };
	//public static SoundEvent soundFizzle;
	private Set neighboursToNotify = new HashSet();
	
	public BlockGunpowderTrail() {
		super("gunpowder_trail", Material.CIRCUITS);
		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(NORTH, EnumAttachPosition.NONE)
				.withProperty(EAST, EnumAttachPosition.NONE)
				.withProperty(SOUTH, EnumAttachPosition.NONE)
				.withProperty(WEST, EnumAttachPosition.NONE)
				.withProperty(BURNING, Integer.valueOf(0)));
		this.setHardness(0.1f);
		this.disableStats();
		this.setSoundType(SoundType.STONE);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return GUNPOWDER_AABB[getAABBIndex(state.getActualState(source, pos))];
	}
	
	private static int getAABBIndex(IBlockState state)
	{
	    int i = 0;
	    boolean flag = state.getValue(NORTH) != EnumAttachPosition.NONE;
	    boolean flag1 = state.getValue(EAST) != EnumAttachPosition.NONE;
	    boolean flag2 = state.getValue(SOUTH) != EnumAttachPosition.NONE;
	    boolean flag3 = state.getValue(WEST) != EnumAttachPosition.NONE;
	    
	    if ((flag) || ((flag2) && (!flag) && (!flag1) && (!flag3)))
	    {
	      i |= 1 << EnumFacing.NORTH.getHorizontalIndex();
	    }
	    
	    if ((flag1) || ((flag3) && (!flag) && (!flag1) && (!flag2)))
	    {
	      i |= 1 << EnumFacing.EAST.getHorizontalIndex();
	    }
	    
	    if ((flag2) || ((flag) && (!flag1) && (!flag2) && (!flag3)))
	    {
	      i |= 1 << EnumFacing.SOUTH.getHorizontalIndex();
	    }
	    
	    if ((flag3) || ((flag1) && (!flag) && (!flag2) && (!flag3)))
	    {
	      i |= 1 << EnumFacing.WEST.getHorizontalIndex();
	    }
	    
	    return i;
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos)
	{
		return NULL_AABB;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@SideOnly(Side.CLIENT)
	public static int colorMultiplier(IBlockState iblockstate)
	{
	    float litAmount = BlockRegistry.gunpowder_trail.getMetaFromState(iblockstate) / 8.0F;
	    float red = litAmount * 0.7F + 0.3F;
	    
	    float green = litAmount * litAmount * 0.4F + 0.3F;
	    float blue = 0.3F;
	    
	    if (green < 0.0F) {
	      green = 0.0F;
	    }
	    
	    if (blue < 0.0F) {
	      blue = 0.0F;
	    }
	    
	    int redInt = MathHelper.clamp_int(MathHelper.floor_float(red * 255.0F), 0, 255); //floor
	    int greenInt = MathHelper.clamp_int(MathHelper.floor_float(green * 255.0F), 0, 255);
	    int blueInt = MathHelper.clamp_int(MathHelper.floor_float(blue * 255.0F), 0, 255);
	    
	    return (redInt << 16) + (greenInt << 8) + blueInt;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
		return colorMultiplier(state);
	}
	
	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos)
	{
		return (world.isSideSolid(pos.down(), EnumFacing.UP)) || (world.getBlockState(pos.down()).getBlock() == Blocks.GLOWSTONE);
	}
	
	private void sendNotifications(World world, BlockPos pos) {
	    ArrayList arraylist = new ArrayList(this.neighboursToNotify);
	    this.neighboursToNotify.clear();
	    
	    for (int l = 0; l < arraylist.size(); l++) {
	        BlockPos notifyPos = (BlockPos)arraylist.get(l);
	        world.notifyNeighborsOfStateChange(notifyPos, this);
	    }
	}
	
	private void notifyNeighbours(World world, BlockPos pos) {
	    if (world.getBlockState(pos).getBlock() == this) {
	        world.notifyNeighborsOfStateChange(pos, this);
	        world.notifyNeighborsOfStateChange(pos.up(), this);
	        world.notifyNeighborsOfStateChange(pos.down(), this);
	        world.notifyNeighborsOfStateChange(pos.north(), this);
	        world.notifyNeighborsOfStateChange(pos.south(), this);
	        world.notifyNeighborsOfStateChange(pos.east(), this);
	        world.notifyNeighborsOfStateChange(pos.west(), this);
	    }
	}
	
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		Integer power = (Integer)state.getValue(BURNING);
	    
	    if (power.intValue() > 0) {
	    	if (power.intValue() >= 8) {
		        world.setBlockToAir(pos);
		        explode(world, pos);
		    } else {
		        if (power.intValue() >= 4) {
		          igniteNeighbours(world, pos);
		        }
		        //TODO
		        //world.scheduleUpdate(pos, state.withProperty(BURNING, Integer.valueOf(power.intValue() + 1)));
		        world.setBlockState(pos, state.withProperty(BURNING, Integer.valueOf(power.intValue() + 1)), 2);
		        world.scheduleUpdate(pos, this, 1);
		    }
		}
	}
	
	public static void explode(World world, BlockPos pos) {
	    //TODO 
		GunpowderTrailExplosion explosion = new GunpowderTrailExplosion(world, pos);
	    if (ForgeEventFactory.onExplosionStart(world, explosion))
	      return;
	    explosion.doExplosion();
	}
	
	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state)
	{
	    super.onBlockAdded(world, pos, state);
	    
	    if (!world.isRemote) {
	        sendNotifications(world, pos);
	        world.notifyNeighborsOfStateChange(pos.up(), this);
	        world.notifyNeighborsOfStateChange(pos.down(), this);
	        notifyNeighbours(world, pos.north());
	        notifyNeighbours(world, pos.south());
	        notifyNeighbours(world, pos.east());
	        notifyNeighbours(world, pos.west());
	      
	        if (world.getBlockState(pos.north()).isNormalCube()) {
	        	notifyNeighbours(world, pos.north().up());
	        } else {
	        	notifyNeighbours(world, pos.north().down());
	        }
	      
	        if (world.getBlockState(pos.south()).isNormalCube()) {
	            notifyNeighbours(world, pos.south().up());
	        } else {
	            notifyNeighbours(world, pos.south().down());
	        }
	      
	        if (world.getBlockState(pos.east()).isNormalCube()) {
	            notifyNeighbours(world, pos.east().up());
	        } else {
	            notifyNeighbours(world, pos.east().down());
	        }
	      
	        if (world.getBlockState(pos.west()).isNormalCube()) {
	            notifyNeighbours(world, pos.west().up());
	        } else {
	            notifyNeighbours(world, pos.west().down());
	        }
	    }
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
	    super.breakBlock(world, pos, state);
	    
	    if (!world.isRemote) {
	        sendNotifications(world, pos);
	        world.notifyNeighborsOfStateChange(pos.up(), this);
	        world.notifyNeighborsOfStateChange(pos.down(), this);
	        notifyNeighbours(world, pos.north());
	        notifyNeighbours(world, pos.south());
	        notifyNeighbours(world, pos.east());
	        notifyNeighbours(world, pos.west());
	      
	        if (world.getBlockState(pos.north()).isNormalCube()) {
	            notifyNeighbours(world, pos.north().up());
	        } else {
	            notifyNeighbours(world, pos.north().down());
	        }
	      
	        if (world.getBlockState(pos.south()).isNormalCube()) {
	            notifyNeighbours(world, pos.south().up());
	        } else {
	            notifyNeighbours(world, pos.south().down());
	        }
	      
	        if (world.getBlockState(pos.east()).isNormalCube()) {
	            notifyNeighbours(world, pos.east().up());
	        } else {
	            notifyNeighbours(world, pos.east().down());
	        } 
	      
	        if (world.getBlockState(pos.west()).isNormalCube()) {
	            notifyNeighbours(world, pos.west().up());
	        } else {
	            notifyNeighbours(world, pos.west().down());
	        }
	    }
	}
	
	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block)
	{
	    if (!world.isRemote) {
	        boolean flag = canPlaceBlockAt(world, pos);
	      
	        if (flag) {
	            sendNotifications(world, pos);
	        } else {
	    	    dropBlockAsItem(world, pos, state, 0);
	            world.setBlockToAir(pos);
	        }
	      
	        super.neighborChanged(state, world, pos, block);
	    }
	    
	    if (checkNeighboursForFire(world, pos)) {
	        ignite(world, pos);
	    }
	}
	
	private boolean checkNeighboursForFire(World world, BlockPos pos)
	{
	    return (world.getBlockState(pos.up()).getBlock() == Blocks.FIRE) 
	    		|| (world.getBlockState(pos.down()).getBlock() == Blocks.FIRE) 
	    		|| (world.getBlockState(pos.north()).getBlock() == Blocks.FIRE) 
	    		|| (world.getBlockState(pos.south()).getBlock() == Blocks.FIRE) 
	    		|| (world.getBlockState(pos.east()).getBlock() == Blocks.FIRE) 
	    		|| (world.getBlockState(pos.west()).getBlock() == Blocks.FIRE);
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
	    return Items.GUNPOWDER;
	}
	
	public static boolean canConnect(IBlockAccess world, BlockPos pos, EnumFacing side) {
	    return world.getBlockState(pos).getBlock() == BlockRegistry.gunpowder_trail
	    		|| world.getBlockState(pos).getBlock() == BlockRegistry.explosive_barrel
	    		|| world.getBlockState(pos).getBlock() == Blocks.TNT;
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity)
	{
	    if (((entity instanceof EntityArrow)) && (!world.isRemote)) {
	        EntityArrow entityarrow = (EntityArrow)entity;
	        if (entityarrow.isBurning()) {
	            ignite(world, pos);
	        }
	    }
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
	    if ((heldItem != null) && (heldItem.getItem() == Items.FLINT_AND_STEEL)) {
	        ignite(world, pos);
	        heldItem.damageItem(1, player);
	        return true;
	    }
	    return super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);
	}
	
	@Override
	public void onBlockDestroyedByExplosion(World world, BlockPos pos, Explosion explosion)
	{
	    if ((!world.isRemote) && (canPlaceBlockAt(world, pos))) {
	      world.setBlockState(pos, getDefaultState());
	      ignite(world, pos);
	    }
	}
	
	private void ignite(World world, BlockPos pos)
	{
	    IBlockState state = world.getBlockState(pos);
	    if (state.getBlock() == this) {
	        Integer power = (Integer)state.getValue(BURNING);
	      
	        if (power.intValue() == 0) {
		        world.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
		        		SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.9F + world.rand  //soundFizzle
		        		.nextFloat() * 0.1F);
		        world.setBlockState(pos, state.withProperty(BURNING, Integer.valueOf(1)));
		        world.scheduleUpdate(pos, this, 1);
	        }
	    }
	}
	
	private void igniteNeighbours(World world, BlockPos pos)
	{
	    ignite(world, pos.up());
	    ignite(world, pos.down());
	    ignite(world, pos.east());
	    ignite(world, pos.west());
	    ignite(world, pos.north());
	    ignite(world, pos.south());
	    
	    if (world.getBlockState(pos.east()).isNormalCube()) {
	        ignite(world, pos.east().up());
	    } else {
	        ignite(world, pos.east().down());
	    }
	    
	    if (world.getBlockState(pos.west()).isNormalCube()) {
	        ignite(world, pos.west().up());
	    } else {
	        ignite(world, pos.west().down());
	    }
	    
	    if (world.getBlockState(pos.north()).isNormalCube()) {
	        ignite(world, pos.north().up());
	    } else {
	        ignite(world, pos.north().down());
	    }
	    
	    if (world.getBlockState(pos.south()).isNormalCube()) {
	        ignite(world, pos.south().up());
	    } else {
	        ignite(world, pos.south().down());
	    }
	}
	
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)
	{
	    int l = getMetaFromState(state);
	    
	    if (l > 0) {
	        double d0 = pos.getX() + 0.5D + (rand.nextFloat() - 0.5D) * 0.5D;
	        double d1 = pos.getY() + 0.0625F;
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
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
	{
	    return new ItemStack(Items.GUNPOWDER);
	}
	
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
	    return getDefaultState().withProperty(BURNING, Integer.valueOf(meta));
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
	    return ((Integer)state.getValue(BURNING)).intValue();
	}
	
	@Override
	protected BlockStateContainer createBlockState()
	{
	    return new BlockStateContainer(this, new IProperty[] { NORTH, EAST, SOUTH, WEST, BURNING });
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
	    state = state.withProperty(WEST, getAttachPosition(worldIn, pos, EnumFacing.WEST));
	    state = state.withProperty(EAST, getAttachPosition(worldIn, pos, EnumFacing.EAST));
	    state = state.withProperty(NORTH, getAttachPosition(worldIn, pos, EnumFacing.NORTH));
	    state = state.withProperty(SOUTH, getAttachPosition(worldIn, pos, EnumFacing.SOUTH));
	    return state;
	}
	
	private EnumAttachPosition getAttachPosition(IBlockAccess worldIn, BlockPos pos, EnumFacing direction) {
	    BlockPos blockpos1 = pos.offset(direction);
	    IBlockState state = worldIn.getBlockState(pos.offset(direction));
	    
	    if ((!canConnect(worldIn, blockpos1, direction)) && ((state.isBlockNormalCube()) || (!canConnect(worldIn, blockpos1.down(), null)))) {
	        IBlockState state1 = worldIn.getBlockState(pos.up());
	        return (!state1.isBlockNormalCube()) && (state.isBlockNormalCube()) && (canConnect(worldIn, blockpos1.up(), null)) 
	        		? EnumAttachPosition.UP : EnumAttachPosition.NONE;
	    }
	    return EnumAttachPosition.SIDE;
	}
	
	static enum EnumAttachPosition implements IStringSerializable
	{
	    UP("up"),  SIDE("side"),  NONE("none");
	    
	    private final String name;
	    
	    private EnumAttachPosition(String name) 
	    {
	    	this.name = name; 
	    }
	    
	    public String toString()
	    {
	        return getName();
	    }
	    
		@Override
		public String getName() {
			return this.name;
		}
	}
}
