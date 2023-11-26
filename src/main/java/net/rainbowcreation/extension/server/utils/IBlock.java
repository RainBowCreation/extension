package net.rainbowcreation.extension.server.utils;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class IBlock {
    public static Material getMaterial(ItemStack stack) {
        Item item = stack.getItem();

        if (item instanceof net.minecraft.item.ItemBlock) {
            Block block = ((net.minecraft.item.ItemBlock) item).getBlock();
            return block.getMaterial(block.getDefaultState());
        }

        // If it's not an ItemBlock, you may need to handle other cases based on your specific items

        return Material.AIR; // Default material if not a block
    }
}
