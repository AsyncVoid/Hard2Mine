package me.Async.hard2mine.registry;

import me.Async.hard2mine.block.BlockHardBase;
import me.Async.hard2mine.block.BlockHardStone;
import me.Async.hard2mine.block.BlockHarderStone;
import me.Async.hard2mine.block.BlockHardestStone;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockRegistry {
	
	public static BlockHardBase hard_stone;
	public static BlockHardBase harder_stone;
	public static BlockHardBase hardest_stone;
	
	public static void init() {
		hard_stone = new BlockHardStone();
		harder_stone = new BlockHarderStone();
		hardest_stone = new BlockHardestStone();
	}
	
	public static void register()
	{
		hard_stone.register();
		harder_stone.register();
		hardest_stone.register();
	}
	
	public static void registerRenderers()
	{
		hard_stone.registerRenderer();
		harder_stone.registerRenderer();
		hardest_stone.registerRenderer();
	}
}
