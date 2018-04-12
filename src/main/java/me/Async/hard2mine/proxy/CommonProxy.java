package me.Async.hard2mine.proxy;

import me.Async.hard2mine.registry.BlockRegistry;
import me.Async.hard2mine.registry.WorldGenRegistry;

public class CommonProxy {
	
	public void preInit() {
        BlockRegistry.init();
        WorldGenRegistry.init();
        BlockRegistry.register();
        WorldGenRegistry.register();
    }

    public void init() {
    	
    }

    public void postInit() {
    	
    }
}
