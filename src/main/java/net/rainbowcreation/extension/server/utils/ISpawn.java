package net.rainbowcreation.extension.server.utils;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ISpawn {
    public static void removeBlockAndSpawnAsItem(World world, BlockPos pos) {
        // Get the block state at the specified position
        BlockPos pos2 = pos.down();
        pos = pos.up();
        IBlockState blockState = world.getBlockState(pos);
        IBlockState blockState2 = world.getBlockState(pos2);
        Block block = blockState.getBlock();
        Block block2 = blockState2.getBlock();

        if (block.getBlockHardness(blockState, world, pos) < 0 || block2.getBlockHardness(blockState2, world, pos2) < 0) {
            return;
        }
        // Set the block state at the specified position to air
        world.destroyBlock(pos, false);
    }

    public static void spawnTNTWithFuse(World world, BlockPos pos, int fuseTime) {
        // Check if the world is not null and is not remote (client-side)
        if (world != null && !world.isRemote) {
            // Create a primed TNT entity at the specified position
            EntityTNTPrimed tnt = new EntityTNTPrimed(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, null);

            // Set the fuse time (in ticks)
            tnt.setFuse(fuseTime);

            // Spawn the TNT entity in the world
            world.spawnEntity(tnt);
        }
    }

    public static void spawnXPOrb(World world, double x, double y, double z, int xp) {
        EntityXPOrb xpOrb = new EntityXPOrb(world, x, y, z, xp);
        world.spawnEntity(xpOrb);
    }

    public static void spawnBlockAsItem(World world, BlockPos pos, Block block) {
        // Create an ItemStack representing the block
        ItemStack itemStack = new ItemStack(block);

        // Spawn the ItemStack as an item entity in the world
        EntityItem entityItem = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, itemStack);
        world.spawnEntity(entityItem);
    }

    public static void spawnLightning(World world, BlockPos pos) {
        // Check if the world is not null and is not remote (client-side)
        if (world != null && !world.isRemote) {
            // Trigger a thunderstorm at the specified position
            world.getWorldInfo().setThundering(true);

            // Optionally, you can also set it to raining
            world.getWorldInfo().setRaining(true);

            // Spawn a lightning bolt at the specified position
            world.addWeatherEffect(new net.minecraft.entity.effect.EntityLightningBolt(world, pos.getX(), pos.getY(), pos.getZ(), false));
        }
    }

    public static void spawnCloneEntity(EntityLivingBase originalEntity) {
        World world = originalEntity.world;

        // Check if the world is not null
        if (world != null && !world.isRemote) {
            // Create a new instance of the same class as the original entity
            EntityLivingBase clonedEntity = cloneEntityInstance(originalEntity);

            // Check if cloning was successful
            if (clonedEntity != null) {
                // Set the position of the cloned entity to the same as the original entity
                clonedEntity.setPosition(originalEntity.posX, originalEntity.posY, originalEntity.posZ);
                // Spawn the cloned entity into the world
                world.spawnEntity(clonedEntity);
            }
        }
    }

    // Helper function to create a new instance of the same class as the original entity
    public static EntityLivingBase cloneEntityInstance(EntityLivingBase originalEntity) {
        Class<? extends EntityLivingBase> entityClass = originalEntity.getClass();

        try {
            // Create a new instance of the entity class
            EntityLivingBase clonedEntity = entityClass.getConstructor(World.class).newInstance(originalEntity.world);

            // Copy NBT data from the original entity to the cloned entity
            NBTTagCompound entityNBT = new NBTTagCompound();
            originalEntity.writeEntityToNBT(entityNBT);
            clonedEntity.readEntityFromNBT(entityNBT);

            return clonedEntity;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /*
    public static void spawnExplosion(World world, BlockPos pos) {
        spawnExplosion(world, pos,3.0f,true);
    }

    public static void spawnExplosion(World world, BlockPos pos, float strength, boolean damagesTerrain) {
        // Check if the world is not null and is not remote (client-side)
        if (world != null && !world.isRemote) {
            // Spawn an explosion at the specified position
            world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), strength, damagesTerrain);
        }
    }
     */
}
