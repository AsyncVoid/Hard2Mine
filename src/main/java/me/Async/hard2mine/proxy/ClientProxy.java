package me.Async.hard2mine.proxy;

import me.Async.hard2mine.registry.BlockRegistry;

public class ClientProxy extends CommonProxy {
	@Override
	public void preInit() {
        super.preInit();
        BlockRegistry.registerRenderers();
    }
	
	@Override
    public void init() {
    	super.init();
    	
    	BlockRegistry.registerBlockColors();
    }
	
	@Override
    public void postInit() {
    	super.postInit();
    }
}
