package net.rainbowcreation.extension.server.event.bungeeteleporter;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.rainbowcreation.extension.server.utils.Reference;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class Sign {
    @SubscribeEvent
    public void onClick(PlayerInteractEvent.LeftClickBlock event) {
        System.out.println("Player interact something");
        if (event.getEntityPlayer().openContainer instanceof net.minecraft.inventory.ContainerPlayer) {
            int slotId = event.getEntityPlayer().openContainer.getSlotFromInventory(event.getEntityPlayer().inventory, event.getEntityPlayer().inventory.currentItem).slotNumber;
            System.out.println("Player clicked slot " + slotId + " in their own inventory.");
        }
    }
}
