package me.Async.hard2mine.world;

import java.util.Random;

import me.Async.hard2mine.registry.BlockRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WorldGenHardStone implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator,
			IChunkProvider chunkProvider) {
		int maxX = (chunkX * 16) + 16;
		int maxZ = (chunkZ * 16) + 16;
		for(int x = chunkX * 16; x <= maxX; x++)
		{
			for(int z = chunkZ * 16; z <= maxZ; z++)
			{
				for(int y = 1; y <= 15; y++)
				{
					BlockPos blockpos = new BlockPos(x, y, z);
                    IBlockState state = world.getBlockState(blockpos);
                    if (state.getBlock() == Blocks.STONE)
                    	world.setBlockState(blockpos, BlockRegistry.hardest_stone.getDefaultState(), 2);
				}
				for(int y = 16; y <= 30; y++)
				{
					BlockPos blockpos = new BlockPos(x, y, z);
                    IBlockState state = world.getBlockState(blockpos);
                    if (state.getBlock() == Blocks.STONE)
                    	world.setBlockState(blockpos, BlockRegistry.harder_stone.getDefaultState(), 2);
				}
				for(int y = 31; y <= 45; y++)
				{
					BlockPos blockpos = new BlockPos(x, y, z);
                    IBlockState state = world.getBlockState(blockpos);
                    if (state.getBlock() == Blocks.STONE)
                    	world.setBlockState(blockpos, BlockRegistry.hard_stone.getDefaultState(), 2);
				}
			}
		}
	}
}
