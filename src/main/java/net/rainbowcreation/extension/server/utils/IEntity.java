package net.rainbowcreation.extension.server.utils;


import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class IEntity {
    public static List<Entity> getNearbyEntities(Entity entity,Class <? extends Entity> entityClass, double radius) {
        World world = entity.getEntityWorld();
        BlockPos pos = entity.getPosition();
        Double x = (double) pos.getX();
        Double y = (double) pos.getY();
        Double z = (double) pos.getZ();
        List<Entity> entityList = world.getEntitiesWithinAABB (entityClass, new AxisAlignedBB(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius));
        return entityList;
    }
}
