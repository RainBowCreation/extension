package net.rainbowcreation.extension.server.event.clearlag;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.rainbowcreation.extension.server.utils.ISpawn;
import net.rainbowcreation.extension.server.utils.Reference;


import static net.rainbowcreation.extension.server.config.ClearLagConfig.clearLag;
@Mod.EventBusSubscriber(modid = Reference.MODID)
public class Xpdrop {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingExperienceDrop(LivingExperienceDropEvent event) {
        if (!clearLag.DISABLE_XP_DROP)
            return;
        EntityPlayer entity = event.getAttackingPlayer(); // Get the player that killed the entity
        if (entity!= null && entity.isEntityAlive()) {
            // Check if the player killed the entity
            // Transfer the experience directly to the player
            //entity.addExperience(event.getDroppedExperience());
            ISpawn.spawnXPOrb(entity.world, entity.posX, entity.posY + 0.5D, entity.posZ, event.getDroppedExperience());
        }
        // Cancel the original event to avoid spawning XP orbs
        event.setCanceled(true);
    }
}
