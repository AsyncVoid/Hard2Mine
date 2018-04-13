package me.Async.hard2mine.registry;

import java.lang.reflect.Field;

import me.Async.hard2mine.block.BlockGunpowderTrail;
import me.Async.hard2mine.block.BlockHardBase;
import me.Async.hard2mine.block.BlockHardStone;
import me.Async.hard2mine.block.BlockHarderStone;
import me.Async.hard2mine.block.BlockHardestStone;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockRegistry {
	
	public static BlockHardBase hard_stone;
	public static BlockHardBase harder_stone;
	public static BlockHardBase hardest_stone;
	public static BlockGunpowderTrail gunpowder_trail;
	
	public static void init() 
	{
		Blocks.STONE.setResistance(0.33f);
		Blocks.STONE.setHardness(2.0f);
		/*Field f;
		try {
			f = Block.class.getDeclaredField("blockResistance"); //field_149781_w
			f.setAccessible(true);
			f.set(Blocks.STONE, 1.0f);
		} catch (Exception ex) {
			ex.printStackTrace();
		}*/
		hard_stone = new BlockHardStone();
		harder_stone = new BlockHarderStone();
		hardest_stone = new BlockHardestStone();
		gunpowder_trail = new BlockGunpowderTrail();
		//Blocks.FIRE.setFireInfo(gunpowder_trail, 100, 100);
	}
	
	public static void register()
	{
		hard_stone.register();
		harder_stone.register();
		hardest_stone.register();
		gunpowder_trail.register();
	}
	
	public static void registerRenderers()
	{
		hard_stone.registerRenderer();
		harder_stone.registerRenderer();
		hardest_stone.registerRenderer();
		gunpowder_trail.registerRenderer();
	}
}
