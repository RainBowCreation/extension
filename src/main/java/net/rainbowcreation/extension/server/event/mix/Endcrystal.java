package net.rainbowcreation.extension.server.event.mix;

import net.minecraftforge.fml.common.Mod;
import net.rainbowcreation.extension.server.utils.Reference;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class Endcrystal {
    // remove Endcrystal that player placed and force place by system
    /*
    @SubscribeEvent
    public static void onEntityJoinWorld(PlayerEvent.StartTracking event) {
        Entity target = event.getTarget();
        if (target instanceof EntityEnderCrystal) {
            Main.LOGGER.info("Tag");
            Main.end_counting+=-1;
            if (Main.end_counting == 4)
                IDragon.placeEndCrystals();
        }
    }

     */
}

