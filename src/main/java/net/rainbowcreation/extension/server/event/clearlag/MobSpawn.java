package net.rainbowcreation.extension.server.event.clearlag;

import com.google.common.collect.Iterables;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.rainbowcreation.extension.server.utils.IEntity;
import net.rainbowcreation.extension.server.utils.IString;
import net.rainbowcreation.extension.server.utils.Reference;

import java.util.List;

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
        }else if (entity.isCreatureType(EnumCreatureType.CREATURE, false)) {
            if (IEntity.isChild(entity))
                return;
            if (IEntity.isRidable(entity))
                return;
            ResourceLocation entityLocation = EntityList.getKey(entity.getClass());
            String entityName = entityLocation != null ? entityLocation.toString() : "";
            String[] split = entityName.split(":");
            String entityDisplayName = split.length > 1 ? split[1] : split[0];
            String type = IString.capitalizeFirstLetter(entityDisplayName);
            // Check the number of entities in the chunk
            List<Entity> entitys = world.getEntitiesWithinAABB(entity.getClass(), entity.getEntityBoundingBox().grow(16.0D));
            int entityCount = entitys.size();
            for (Entity entityc : entitys) {
                if (IEntity.isChild(entityc))
                    return;
                if (Iterables.size(entity.getArmorInventoryList()) == 0 || Iterables.size(entityc.getArmorInventoryList()) == 0)
                    return;
                switch (type) {
                    case ("Sheep"): {
                        if (((EntitySheep) entity).getFleeceColor() != ((EntitySheep) entityc).getFleeceColor())
                            return;
                        break;
                    }
                    case ("Rabbit"): {
                        // s
                        break;
                    }
                }
                String nametag = entityc.getCustomNameTag();
                StringBuilder sb = new StringBuilder(nametag);
                if (nametag.contains("X")) {
                    entityCount += Integer.parseInt(nametag.substring(sb.lastIndexOf(" X") + 2)) - 1;
                }
                entityc.setDead();
            }
            entityCount += 1;
            entity.setCustomNameTag(type + " X" + entityCount);
        }
    }
}