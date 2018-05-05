package me.Async.hard2mine.explosion;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class GunpowderTrailExplosion extends Explosion {

	private final World world;
	private final BlockPos location;
	private static final float size = 1.0f;
	
	public GunpowderTrailExplosion(World worldIn, BlockPos pos) {
		super(worldIn, null, pos.getX(), pos.getY(), pos.getZ(), size/2, true, false);
		location = pos;
		world = worldIn;
	}
	
	public void doExplosion()
	{
	    int x = location.getX();
	    int y = location.getY();
	    int z = location.getZ();
	    
	    float actualX = x + 0.5f;
	    float actualZ = z + 0.5f;
	    
	    int minX = MathHelper.floor_double(actualX - size - 1.0D);
	    int minY = MathHelper.floor_double(y - size - 1.0D);
	    int minZ = MathHelper.floor_double(actualZ - size - 1.0D);
	    int maxX = MathHelper.floor_double(actualX + size + 1.0D);
	    int maxY = MathHelper.floor_double(y + size + 1.0D);
	    int maxZ = MathHelper.floor_double(actualZ + size + 1.0D);
	    List list = this.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ));
	    
	    ForgeEventFactory.onExplosionDetonate(this.world, this, list, size);
	    Vec3d vec3 = new Vec3d(x, y, z);
	    
	    for (int i = 0; i < list.size(); i++) {
	      Entity entity = (Entity)list.get(i);
	      double distanceToExplosion = entity.getDistance(x, y, z) / size;
	      

	      if (distanceToExplosion <= 1.0D) {
	        double diffX = entity.posX - actualX;
	        double diffY = entity.posY + entity.getEyeHeight() - y;
	        double diffZ = entity.posZ - actualZ;
	        double displacement = MathHelper.sqrt_double(diffX * diffX + diffY * diffY + diffZ * diffZ);
	        
	        if (displacement != 0.0D) {
	          diffX /= displacement;
	          diffY /= displacement;
	          diffZ /= displacement;
	          double blockDensity = this.world.getBlockDensity(vec3, entity.getEntityBoundingBox());
	          double damageAmount = (1.0D - distanceToExplosion) * blockDensity;
	          entity.attackEntityFrom(DamageSource.causeExplosionDamage(this), (int)((damageAmount * damageAmount + damageAmount) / 2.0D * 8.0D * size + 1.0D));
	          

	          double protectionMultiplier = (entity instanceof EntityLivingBase) ? 
	        		  EnchantmentProtection.getBlastDamageReduction((EntityLivingBase)entity, damageAmount) : 1.0D;
	          entity.motionX += diffX * protectionMultiplier;
	          entity.motionY += diffY * protectionMultiplier;
	          entity.motionZ += diffZ * protectionMultiplier;
	          
	          if ((entity instanceof EntityPlayer)) {
	        	  getPlayerKnockbackMap().put((EntityPlayer)entity, new Vec3d(diffX * damageAmount, diffY * damageAmount, diffZ * damageAmount));
	          }
	        }
	      }
	    }
	    

	    explodeBlock(x + 1, y, z);
	    explodeBlock(x - 1, y, z);
	    explodeBlock(x, y + 1, z);
	    explodeBlock(x, y - 1, z);
	    explodeBlock(x, y, z + 1);
	    explodeBlock(x, y, z - 1);
	    
	    explodeBlock(x + 1, y - 1, z);
	    explodeBlock(x - 1, y - 1, z);
	    explodeBlock(x, y - 1, z + 1);
	    explodeBlock(x, y - 1, z - 1);
	    
	    if (Blocks.FIRE.canCatchFire(this.world, new BlockPos(x, y - 1, z), EnumFacing.UP)) {
	      this.world.setBlockState(new BlockPos(x, y, z), Blocks.FIRE.getDefaultState());
	    }
	  }
	  
	  public void explodeBlock(int x, int y, int z) {
	    BlockPos pos = new BlockPos(x, y, z);
	    IBlockState blockState = this.world.getBlockState(pos);
	    Block block = blockState.getBlock();
	    
	    if ((block.getExplosionResistance(null) == 0.0F) && (block != Blocks.FIRE) && 
	      (blockState.getMaterial() != Material.AIR)) {
	      if (block.canDropFromExplosion(this)) {
	        block.dropBlockAsItemWithChance(this.world, pos, blockState, 1.0F / size, 0);
	      }
	      
	      block.onBlockExploded(this.world, pos, this);
	    }
	  }
}
