package me.Async.hard2mine;

import me.Async.hard2mine.proxy.CommonProxy;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Hard2Mine.MODID, version = Hard2Mine.VERSION, acceptedMinecraftVersions=Hard2Mine.MCVERSIONS)
public class Hard2Mine
{
    public static final String MODID = "hard2mine";
    public static final String VERSION = "0.1";
    public static final String MCVERSIONS = "[1.10.2]";
    public static final String PROXY_CLIENT = "me.Async.hard2mine.proxy.ClientProxy";
    public static final String PROXY_COMMON = "me.Async.hard2mine.proxy.CommonProxy";
    
    @SidedProxy(clientSide = Hard2Mine.PROXY_CLIENT, serverSide = Hard2Mine.PROXY_COMMON)
    private static CommonProxy proxy;
    
    
    @EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
    	proxy.preInit();
	}
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	proxy.init();
    }
    
    @EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
    	proxy.postInit();
	}
}
