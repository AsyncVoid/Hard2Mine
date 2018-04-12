package me.Async.hard2mine.block;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockHardestStone extends BlockHardBase {
	
	public BlockHardestStone() {
		super("hardest_stone", Material.ROCK);
		super.blockHardness = 8.0f;
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
		if(stack == null || !(stack.getItem() instanceof ItemTool)) {
			this.blockHardness = -1.0f;
			return;
		}
		ItemTool item = (ItemTool) stack.getItem();
		IBlockState state = worldIn.getBlockState(pos);
		int harvestlevel = item.getHarvestLevel(stack, "pickaxe", playerIn, state);
		if(harvestlevel < 3){
			this.blockHardness = -1.0f;
			return;
		}
		this.blockHardness = 8.0f;
    }
}

