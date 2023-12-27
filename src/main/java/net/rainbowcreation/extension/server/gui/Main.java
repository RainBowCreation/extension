package net.rainbowcreation.extension.server.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.rainbowcreation.extension.server.network.Bungee;
import net.rainbowcreation.extension.server.utils.Chat;
import net.rainbowcreation.extension.server.utils.IPacket;
import net.rainbowcreation.extension.server.utils.Item;

import java.util.HashMap;
import java.util.Map;

public class Main {
    private net.rainbowcreation.extension.server.Main main;
    private static InventoryBasic gui = null;
    private static Map<EntityPlayer, Boolean> is_move = new HashMap<>();
    public Main() {
        main = net.rainbowcreation.extension.server.Main.getInstance();
    }

    public InventoryBasic get() {
        if (gui != null)
            return gui;
        gui = new InventoryBasic("Main", true, 54);
        gui.setInventorySlotContents(44, new Item().material(Blocks.BARRIER).displayName("<white> ").get());
        gui.setInventorySlotContents(27, new Item().material(Blocks.STAINED_GLASS_PANE).color(5).displayName("Warps").lore("Left-Click <white>to warp to <green>Lobby").get());
        gui.setInventorySlotContents(28, new Item().material(Blocks.STAINED_GLASS_PANE).displayName("Mainnet (survival)").lore("Left-Click <white>to warp").lore("<white>recommend version <green>1.20.1+").get());
        gui.setInventorySlotContents(29, new Item().material(Blocks.STAINED_GLASS_PANE).displayName("RLCraft").lore("Left-Click <white>to warp").lore("<white>need RLCraft version <green>2.9.3").get());
        gui.setInventorySlotContents(30, new Item().material(Blocks.STAINED_GLASS_PANE).displayName("StoneBlock3").lore("Left-Click <white>to warp").lore("<white>need StoneBlock3 version <green>1.8.1").get());
        gui.setInventorySlotContents(31, new Item().material(Blocks.STAINED_GLASS_PANE).displayName("<white>Our partner").lore("Left-Click <white>to warp to <green>morphedit.online").lore("<white>recommend version <green>1.20.1+").get());
        return gui;
    }

    public InventoryBasic getDynamic(EntityPlayer player) {
        if (!isDynamic())
            return get();
        return null;
    }

    public boolean isDynamic() {
        return false;
    }

    public void onClick(PlayerContainerEvent event, int slot) {
        event.setCanceled(true);
        final EntityPlayer player = event.getEntityPlayer();
        if (slot == 44) {
            player.getServer().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    while (!Thread.currentThread().isInterrupted()) {
                        // continue processing
                        try {
                            Thread.sleep(100);
                            player.closeScreen();
                            Thread.currentThread().interrupt();
                        } catch (InterruptedException e) {
                            // good practice
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                }
            });
            return;
        }
        String server = null;
        if (slot == 27)
            server = "lobby";
        else if (slot == 28)
            server = "mainnet";
        else if (slot == 29)
            server = "rlcraft";
        else if (slot == 30)
            server = "stoneblock";
        else if (slot == 31)
            server = "morph";
        if (server != null) {
            String finalServer = server;
            final int[] count = {0};
            is_move.put(player, false);
            player.getServer().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    while (!Thread.currentThread().isInterrupted()) {
                        // continue processing
                        try {
                            if (count[0] == 0) {
                                Thread.sleep(100);
                                player.closeScreen();
                            } else if (count[0] < 5) {
                                IPacket.sent((EntityPlayerMP) player, Chat.minimessageText("<white>Preparing teleport <red>" + (5 - count[0])), SPacketTitle.Type.TITLE, 20, 100, 20);
                                IPacket.sent((EntityPlayerMP) player, Chat.minimessageText("<red>Do not move"), SPacketTitle.Type.SUBTITLE, 20, 100, 20);
                                count[0]++;
                                if (is_move.get(player)) {
                                    player.sendMessage(Chat.minimessageText("<red>Warp Cancelled"));
                                    is_move.remove(player);
                                    Thread.currentThread().interrupt();
                                }
                            }
                            else {
                                Bungee.sendPlayerToServer((EntityPlayerMP) player, finalServer);
                                is_move.remove(player);
                                Thread.currentThread().interrupt();
                            }
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            // good practice
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                }
            });
        }
    }
}
