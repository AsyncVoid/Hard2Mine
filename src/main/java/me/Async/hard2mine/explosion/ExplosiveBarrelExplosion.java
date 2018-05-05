package me.Async.hard2mine.explosion;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class ExplosiveBarrelExplosion extends Explosion {

	private final World world;
	private static final float size = 6.0f;
	
	public ExplosiveBarrelExplosion(World worldIn, BlockPos pos) {
		super(worldIn, null, pos.getX(), pos.getY(), pos.getZ(), size, false, true);
		world = worldIn;
	}
	
	@Override
	public void doExplosionA()
	{
		Vec3d location = this.getPosition();
	    int x = MathHelper.floor_double(location.xCoord);
	    int y = MathHelper.floor_double(location.yCoord);
	    int z = MathHelper.floor_double(location.zCoord);
	    
	    Set<BlockPos> set = Sets.<BlockPos>newHashSet();
        //int i = 16;

        for (int j = 0; j < 16; ++j)
        {
            for (int k = 0; k < 16; ++k)
            {
                for (int l = 0; l < 16; ++l)
                {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15)
                    {
                        double d0 = (double)((float)j / 15.0F * 2.0F - 1.0F);
                        double d1 = (double)((float)k / 15.0F * 2.0F - 1.0F);
                        double d2 = (double)((float)l / 15.0F * 2.0F - 1.0F);
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 = d0 / d3;
                        d1 = d1 / d3;
                        d2 = d2 / d3;
                        float f = size * (0.7F + this.world.rand.nextFloat() * 0.6F);
                        double d4 = x;
                        double d6 = y;
                        double d8 = z;

                        for (float f1 = 0.3F; f > 0.0F; f -= 0.22500001F)
                        {
                            BlockPos blockpos = new BlockPos(d4, d6, d8);
                            IBlockState iblockstate = this.world.getBlockState(blockpos);

                            if (iblockstate.getMaterial() != Material.AIR)
                            {
                                float f2 = iblockstate.getBlock().getExplosionResistance(world, blockpos, (Entity)null, this);
                                f -= (f2 + 0.3F) * 0.3F;
                            }

                            if (f > 0.0)
                            {
                                set.add(blockpos);
                            }

                            d4 += d0 * 0.30000001192092896D;
                            d6 += d1 * 0.30000001192092896D;
                            d8 += d2 * 0.30000001192092896D;
                        }
                    }
                }
            }
        }

        getAffectedBlockPositions().addAll(set);
	    
	    float actualX = x + 0.5f;
	    float actualY = y + 0.5f;
	    float actualZ = z + 0.5f;
	    float explosionSize = size * 2;
	    
        int minX = MathHelper.floor_double(x - (double)explosionSize - 1.0D);
        int maxX = MathHelper.floor_double(x + (double)explosionSize + 1.0D);
        int minY = MathHelper.floor_double(y - (double)explosionSize - 1.0D);
        int maxY = MathHelper.floor_double(y + (double)explosionSize + 1.0D);
        int minZ = MathHelper.floor_double(z - (double)explosionSize - 1.0D);
        int maxZ = MathHelper.floor_double(z + (double)explosionSize + 1.0D);
        
        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB((double)minX, (double)minY, (double)minZ, (double)maxX, (double)maxY, (double)maxZ));
        net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.world, this, list, explosionSize);
        Vec3d vec3d = new Vec3d(actualX, actualY, actualZ);

        for (int k2 = 0; k2 < list.size(); ++k2)
        {
            Entity entity = (Entity)list.get(k2);

            if (!entity.isImmuneToExplosions())
            {
                double d12 = entity.getDistance(x, y, z) / (double)explosionSize;

                if (d12 <= 1.0D)
                {
                    double d5 = entity.posX - actualX;
                    double d7 = entity.posY + (double)entity.getEyeHeight() - y;
                    double d9 = entity.posZ - actualZ;
                    double d13 = (double)MathHelper.sqrt_double(d5 * d5 + d7 * d7 + d9 * d9);

                    if (d13 != 0.0D)
                    {
                        d5 = d5 / d13;
                        d7 = d7 / d13;
                        d9 = d9 / d13;
                        double d14 = (double)this.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
                        double d10 = (1.0D - d12) * d14;
                        if(!(entity instanceof EntityItem))
                        entity.attackEntityFrom(DamageSource.causeExplosionDamage(this), (float)((int)((d10 * d10 + d10) / 2.0D * 7.0D * (double)explosionSize + 1.0D)));
                        double d11 = 1.0D;

                        if (entity instanceof EntityLivingBase)
                        {
                            d11 = EnchantmentProtection.getBlastDamageReduction((EntityLivingBase)entity, d10);
                        }

                        entity.motionX += d5 * d11;
                        entity.motionY += d7 * d11;
                        entity.motionZ += d9 * d11;

                        if (entity instanceof EntityPlayer)
                        {
                            EntityPlayer entityplayer = (EntityPlayer)entity;

                            if (!entityplayer.isSpectator() && (!entityplayer.isCreative() || !entityplayer.capabilities.isFlying))
                            {
                            	getPlayerKnockbackMap().put(entityplayer, new Vec3d(d5 * d10, d7 * d10, d9 * d10));
                            }
                        }
                    }
                }
            }
        }
	}
	
	@Override
	public void doExplosionB(boolean spawnParticles)
    {
		Vec3d location = getPosition();
		double explosionX = location.xCoord;
		double explosionY = location.yCoord;
		double explosionZ = location.zCoord;
		
        this.world.playSound((EntityPlayer)null, explosionX, explosionY, explosionZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F);

        this.world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, explosionX, explosionY, explosionZ, 1.0D, 0.0D, 0.0D, new int[0]);
        
        for (BlockPos blockpos : getAffectedBlockPositions())
        {
            IBlockState iblockstate = this.world.getBlockState(blockpos);
            Block block = iblockstate.getBlock();

            if (spawnParticles)
            {
                double d0 = (double)((float)blockpos.getX() + this.world.rand.nextFloat());
                double d1 = (double)((float)blockpos.getY() + this.world.rand.nextFloat());
                double d2 = (double)((float)blockpos.getZ() + this.world.rand.nextFloat());
                double d3 = d0 - explosionX;
                double d4 = d1 - explosionY;
                double d5 = d2 - explosionZ;
                double d6 = (double)MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
                d3 = d3 / d6;
                d4 = d4 / d6;
                d5 = d5 / d6;
                double d7 = 0.5D / (d6 / (double)size + 0.1D);
                d7 = d7 * (double)(this.world.rand.nextFloat() * this.world.rand.nextFloat() + 0.3F);
                d3 = d3 * d7;
                d4 = d4 * d7;
                d5 = d5 * d7;
                this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, (d0 + explosionX) / 2.0D, (d1 + explosionY) / 2.0D, (d2 + explosionZ) / 2.0D, d3, d4, d5, new int[0]);
                this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, d3, d4, d5, new int[0]);
            }

            if (iblockstate.getMaterial() != Material.AIR)
            {
                if (block.canDropFromExplosion(this))
                {
                    block.dropBlockAsItemWithChance(this.world, blockpos, this.world.getBlockState(blockpos), 1.0F /*/ this.explosionSize*/, 0);
                }

                block.onBlockExploded(this.world, blockpos, this);
            }
        }

        
        if (this.world.getBlockState(new BlockPos(explosionX, explosionY - 1, explosionZ)).isFullBlock())
        {
            this.world.setBlockState(new BlockPos(explosionX, explosionY, explosionZ), Blocks.FIRE.getDefaultState());
        }
    }
	
	
	public void explodeBlock(int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
	    IBlockState blockState = this.world.getBlockState(pos);
	    Block block = blockState.getBlock();
	    
	    if ((block.getExplosionResistance(null) <= 30.0F) 
	    		&& (block != Blocks.FIRE) 
	    		&& (blockState.getMaterial() != Material.AIR)) {
	    	if (block.canDropFromExplosion(this)) {
	    		block.dropBlockAsItemWithChance(this.world, pos, blockState, 1.0F, 0);
	        }
	      
	        block.onBlockExploded(this.world, pos, this);
	    }
	}
}
