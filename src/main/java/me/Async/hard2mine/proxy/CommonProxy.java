package me.Async.hard2mine.proxy;

import me.Async.hard2mine.event.PlayerInteractEventHandler;
import me.Async.hard2mine.registry.BlockRegistry;
import me.Async.hard2mine.registry.WorldGenRegistry;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy {
	
	public void preInit() {
        BlockRegistry.init();
        WorldGenRegistry.init();
        BlockRegistry.register();
        WorldGenRegistry.register();
    }

    public void init() {
    	MinecraftForge.EVENT_BUS.register(new PlayerInteractEventHandler());
    }

    public void postInit() {
    	
    }
}
