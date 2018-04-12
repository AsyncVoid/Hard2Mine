package me.Async.hard2mine.registry;

import me.Async.hard2mine.world.WorldGenHardStone;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class WorldGenRegistry {

	public static IWorldGenerator hard_stone;
	
	public static void init()
	{
		hard_stone = new WorldGenHardStone();
	}
	
	public static void register()
	{
		GameRegistry.registerWorldGenerator(hard_stone, 10);
	}
}
