package net.rainbowcreation.extension.server.utils;

import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class IDragon {
    public static void placeEndCrystals() {
        WorldServer worldServer = DimensionManager.getWorld(1); // Dimension ID 1 is the End in Minecraft

        if (worldServer != null) {
            // Coordinates of the central End Portal altar
            BlockPos centralPos = new BlockPos(0, 63, 0);

            // Place End Crystals around the altar
            placeEndCrystal(worldServer, centralPos.add(2, 0, 0));
            placeEndCrystal(worldServer, centralPos.add(-2, 0, 0));
            placeEndCrystal(worldServer, centralPos.add(0, 0, 2));
            placeEndCrystal(worldServer, centralPos.add(0, 0, -2));
        }
    }

    private static void placeEndCrystal(World world, BlockPos pos) {
        // Check if the block is air before placing the End Crystal
        if (world.isAirBlock(pos)) {
            // Place the End Crystal
            //world.setBlockState(pos, Blocks.END_CRYSTAL .getDefaultState());

            // Spawn an EntityEnderCrystal at the same location
            EntityEnderCrystal endCrystal = new EntityEnderCrystal(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            world.spawnEntity(endCrystal);
        }
    }
}
