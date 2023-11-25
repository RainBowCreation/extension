package net.rainbowcreation.extension.server.utils;


import jdk.jfr.Frequency;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static List<List<String>> groupEntitiesByNameTag(World world, List<Entity> entityList, double radius) {
        List<List<String>> groupedEntities = new ArrayList<>();

        Map<Class<? extends Entity>, List<Entity>> entityMap = new HashMap<>();

        // Group entities by their class type
        for (Entity entity : entityList) {
            if (entity instanceof EntityAnimal) {
                Class<? extends Entity> entityClass = entity.getClass();
                entityMap.computeIfAbsent(entityClass, k -> new ArrayList<>()).add(entity);
            }
        }

        // Check each group within the specified radius
        for (List<Entity> entities : entityMap.values()) {
            List<String> entityGroup = new ArrayList<>();

            for (Entity entity : entities) {
                // Check if the entity is already part of a group
                if (entityGroup.contains(entity.getCustomNameTag())) {
                    continue;
                }

                // Group entities within the specified radius
                String groupedName = groupEntitiesWithinRadius(entity, entities, radius);
                entityGroup.add(groupedName);
            }

            groupedEntities.add(entityGroup);
        }

        return groupedEntities;
    }

    private static String groupEntitiesWithinRadius(Entity anchorEntity, List<Entity> entities, double radius) {
        int count = 0;

        for (Entity entity : entities) {
            if (entity != anchorEntity && entity.getDistance(anchorEntity) <= radius) {
                count++;
            }
        }

        if (count > 0) {
            String entityName = anchorEntity.getClass().getSimpleName() + " X" + (count + 1);
            anchorEntity.setCustomNameTag(entityName);
            anchorEntity.setAlwaysRenderNameTag(true);
            entities.removeIf(entity -> entity.getDistance(anchorEntity) <= radius);
            return entityName;
        } else {
            return anchorEntity.getCustomNameTag();
        }
    }

    public static boolean isChild(Entity entity) {
        if (entity instanceof EntityAgeable) {
            EntityAgeable ageable = (EntityAgeable) entity;
            return ageable.isChild();
        }
        return false;
    }

    public static boolean isRidable(Entity entity) {
        if (entity instanceof EntityLiving) {
            if (entity instanceof EntityHorse)
                return true;
        }
        return false;
    }
}
