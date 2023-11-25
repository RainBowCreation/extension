package net.rainbowcreation.extension.server.event.clearlag;

import com.google.common.collect.Iterables;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityAnimal;
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
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.rainbowcreation.extension.server.Main;
import net.rainbowcreation.extension.server.utils.IEntity;
import net.rainbowcreation.extension.server.utils.ISpawn;
import net.rainbowcreation.extension.server.utils.IString;
import net.rainbowcreation.extension.server.utils.Reference;

import java.util.List;

import static net.rainbowcreation.extension.server.config.ClearLagConfig.clearLag;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class MobSpawn {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (clearLag.MONSTER_LIMIT == 0)
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
        }else if (entity.isCreatureType(EnumCreatureType.CREATURE, false))
            IEntity.setUpEntity(entity);
    }

    @SubscribeEvent
    public static void onEntityFeed(PlayerInteractEvent.EntityInteract event) {
        Entity entity = event.getTarget();
        if (IEntity.isInLove(entity)) {
            Main.LOGGER.info(entity.getName() + " is in love!");
            String type = IEntity.getType(entity);
            if (entity instanceof EntityAnimal) {
                EntityLivingBase animal = (EntityLivingBase) entity;
                EntityLivingBase clone = ISpawn.cloneEntityInstance(animal);
                if (clone instanceof EntityAnimal) {
                    ((EntityAnimal) clone).setInLove(null);
                }
                entity.world.spawnEntity(clone);
                //ISpawn.spawnCustomNamedEntity(entity, type + " X2");
            }
        }
    }
}