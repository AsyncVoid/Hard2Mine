package me.Async.hard2mine.registry;

import me.Async.hard2mine.block.BlockHardBase;
import me.Async.hard2mine.block.BlockHardStone;
import me.Async.hard2mine.block.BlockHarderStone;
import me.Async.hard2mine.block.BlockHardestStone;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockRegistry {
	
	private static BlockHardBase hardStone;
	private static BlockHardBase harderStone;
	private static BlockHardBase hardestStone;
	
	public static void init() {
		hardStone = new BlockHardStone();
		harderStone = new BlockHarderStone();
		hardestStone = new BlockHardestStone();
	}
	
	public static void register()
	{
		hardStone.register();
		harderStone.register();
		hardestStone.register();
	}
	
	public static void registerRenderers()
	{
		hardStone.registerRenderer();
		harderStone.registerRenderer();
		hardestStone.registerRenderer();
	}
}
