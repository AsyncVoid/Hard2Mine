package me.Async.hard2mine.block;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockHardStone extends ModBlock {

	public BlockHardStone() {
		super("hard_stone", Material.ROCK);
		super.blockHardness = 4.0f;
	}
	
	@Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(Blocks.COBBLESTONE);
    }
	
	@Override
	public boolean isReplaceableOreGen(IBlockState state, IBlockAccess world, BlockPos pos, com.google.common.base.Predicate<IBlockState> target)
    {
        return Blocks.STONE.isReplaceableOreGen(state, world, pos, target);
    }
	
	@SideOnly(Side.CLIENT)
	public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn)
    {
		ItemStack stack = playerIn.getHeldItemMainhand();
		if(stack == null || !(stack.getItem() instanceof Item)) { //ItemTool
			this.blockHardness = -1.0f;
			//System.out.println(stack.getItem().getClass().getName());
			return;
		}
		Item item = stack.getItem();
		IBlockState state = worldIn.getBlockState(pos);
		int harvestlevel = item.getHarvestLevel(stack, "pickaxe", playerIn, state);
		//System.out.println(item.getClass().getName() + ":" + harvestlevel);
		if(harvestlevel < 1){
			this.blockHardness = -1.0f;
			return;
		}
		this.blockHardness = 4.0f;
    }
}
