package net.rainbowcreation.extension.server.event.corrupted;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketResourcePackSend;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.rainbowcreation.extension.server.utils.ITeam;


public class handler {
    public static void onJoin(PlayerEvent.PlayerLoggedInEvent event) {
        ((EntityPlayerMP) event.player).connection.sendPacket(new SPacketResourcePackSend("https://github.com/RainBowCreation/resourcepack/releases/latest/download/RainBowCreation_v1_11.zip" + "?timestamp=" + System.currentTimeMillis(), ""));
        MinecraftServer server = event.player.getEntityWorld().getMinecraftServer();
        ITeam.joinTeam(server, event.player.getName(), "Non-Combatant");
    }

    public static void onHurt(LivingHurtEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer) || !(event.getSource().getTrueSource() instanceof EntityPlayer))
            return;
        EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
        if (player.getName().equals(event.getEntityLiving().getName()))
            return;
        MinecraftServer server = player.getEntityWorld().getMinecraftServer();
        ITeam.joinTeam(server, player.getName(), "Combatant");
    }

    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer) || !(event.getSource().getTrueSource() instanceof EntityPlayer))
            return;
        EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
        if (player.getName().equals(event.getEntityLiving().getName()))
            return;
        MinecraftServer server = player.getEntityWorld().getMinecraftServer();
        ITeam.joinTeam(server, player.getName(), "Corrupted");
        ITeam.joinTeam(server, event.getEntityLiving().getName(), "Non-Combatant");
    }
}
