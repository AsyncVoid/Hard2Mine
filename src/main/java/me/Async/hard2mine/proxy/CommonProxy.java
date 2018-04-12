package me.Async.hard2mine.proxy;

import me.Async.hard2mine.registry.BlockRegistry;

public class CommonProxy {
	
	public void preInit() {
        BlockRegistry.init();
        
        BlockRegistry.register();
    }

    public void init() {
    	
    }

    public void postInit() {
    	
    }
}
