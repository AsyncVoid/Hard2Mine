package me.Async.hard2mine.event;

import me.Async.hard2mine.registry.BlockRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerInteractEventHandler {

	  @SubscribeEvent
	  public void placeGunpowder(PlayerInteractEvent.RightClickBlock event)
	  {
	      if ((event.getItemStack() != null) && (event.getItemStack().getItem() == Items.GUNPOWDER)) {
		      BlockPos shifted = new BlockPos(event.getPos());
		      IBlockState state = event.getWorld().getBlockState(event.getPos());
	
		      if (!state.getBlock().isReplaceable(event.getWorld(), event.getPos())) {
		          if (event.getFace() == EnumFacing.DOWN) {
		        	  shifted = shifted.down();
		          }
		          if (event.getFace() == EnumFacing.UP) {
		        	  shifted = shifted.up();
		          }
		          if (event.getFace() == EnumFacing.NORTH) {
		        	  shifted = shifted.north();
		          }
		          if (event.getFace() == EnumFacing.SOUTH) {
		        	  shifted = shifted.south();
		          }
		          if (event.getFace() == EnumFacing.EAST) {
		        	  shifted = shifted.east();
		          }
		          if (event.getFace() == EnumFacing.WEST) {
		        	  shifted = shifted.west();
		          }
		        
		          if ((!event.getWorld().isAirBlock(shifted)) 
		        		  && (!event.getWorld().getBlockState(shifted).getBlock().isReplaceable(event.getWorld(), shifted))) {
		        	  return;
		          }
		      }
		      
	
		      if (!event.getEntityPlayer().canPlayerEdit(shifted, event.getFace(), event.getItemStack())) {
		        return;
		      }
		      if (BlockRegistry.gunpowder_trail.canPlaceBlockAt(event.getWorld(), shifted))
		      {
		          if (!event.getEntityPlayer().capabilities.isCreativeMode) {
		        	  event.getItemStack().stackSize -= 1;
		          }
		          event.getWorld().setBlockState(shifted, BlockRegistry.gunpowder_trail.getDefaultState());
		      }
	      }
	  }
}
