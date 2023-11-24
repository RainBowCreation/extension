package net.rainbowcreation.extension.server.event.clearlag;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.rainbowcreation.extension.server.utils.Reference;

import static net.rainbowcreation.extension.server.config.ClearLagConfig.clearLag;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class MobSpawn {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (clearLag.MONSTER_LIMIT == -1)
            return;
        World world = event.getWorld();
        Entity entity = event.getEntity();

        // Check if the entity is a mob (you can customize this check based on your needs)
        if (entity.isCreatureType(EnumCreatureType.MONSTER, false)) {
            // Check the number of entities in the chunk
            int entityCount = world.getEntitiesWithinAABB(entity.getClass(), entity.getEntityBoundingBox().grow(16.0D)).size();
            // If the entity count exceeds the limit, cancel the spawn event
            if (entityCount >= clearLag.MONSTER_LIMIT) {
                event.setCanceled(true);
            }
        }
    }
}
