package me.Async.hard2mine.block;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

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
}

