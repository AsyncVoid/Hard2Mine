package me.Async.hard2mine.block;

import java.util.Random;

import me.Async.hard2mine.Hard2Mine;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlock extends Block {

	public ModBlock(String name, Material blockMaterialIn) {
		super(blockMaterialIn);
		this.setUnlocalizedName(name);
		this.setRegistryName(Hard2Mine.MODID, name);
	}
	
	public ModBlock(String name, Material blockMaterialIn, MapColor blockMapColorIn) {
		super(blockMaterialIn, blockMapColorIn);
		this.setUnlocalizedName(name);
		this.setRegistryName(Hard2Mine.MODID, name);
	}
	
	public void register() {
        GameRegistry.register(this);
        GameRegistry.register(new ItemBlock(this), this.getRegistryName());
    }
	
	@SideOnly(Side.CLIENT)
    public void registerRenderer() {
		System.out.println("--------------------------------------------");
		System.out.println(Item.getItemFromBlock(this));
		System.out.println(((ItemBlock)Item.getItemFromBlock(this)).getBlock());
		System.out.println(this.getRegistryName());
		System.out.println("--------------------------------------------");
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this),
                0,
                new ModelResourceLocation(this.getRegistryName(), "inventory")
        );
    }
}
