package net.rainbowcreation.extension.server.event.requiemsleep;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.rainbowcreation.extension.server.Main;
import net.rainbowcreation.extension.server.utils.Reference;

import static net.rainbowcreation.extension.server.config.RequiemConfig.requiem;
@Mod.EventBusSubscriber(modid = Reference.MODID)
public class Requiem {
    public static int speed = 1;
    @SubscribeEvent
    public static void onSleep(PlayerSleepInBedEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        World world = player.getEntityWorld();
        Main.LOGGER.info(getTime(world.getWorldTime()));
        if (getTime(world.getWorldTime()) < 12541)
            return;
        Main.sleepList.add(player);
        MinecraftServer server = world.getMinecraftServer();
        int percent = calculatePercent(Main.sleepList.size(), server.getCurrentPlayerCount());
        calculateSpeed(percent);
        Main.LOGGER.info(speed);
        server.getPlayerList().sendMessage(new TextComponentString(TextFormatting.BOLD + "[Requiem Sleep] ").appendSibling(player.getDisplayName()).appendSibling(new TextComponentString(" is sleeping " + TextFormatting.RED + Main.sleepList.size() + "/" + server.getCurrentPlayerCount() + " " + percent + "%")));
    }

    @SubscribeEvent
    public static void onWake(PlayerWakeUpEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        Main.sleepList.remove(player);
        World world = player.getEntityWorld();
        MinecraftServer server = world.getMinecraftServer();
        int percent = calculatePercent(Main.sleepList.size(), server.getCurrentPlayerCount());
        calculateSpeed(percent);
        Main.LOGGER.info(speed);
        Main.LOGGER.info(getTime(world.getWorldTime()));
        if (getTime(world.getWorldTime()) < 12541)
            return;
        server.getPlayerList().sendMessage(new TextComponentString(TextFormatting.BOLD + "[Requiem Sleep] ").appendSibling(player.getDisplayName()).appendSibling(new TextComponentString(" is no longer sleeping " + TextFormatting.RED + Main.sleepList.size() + "/" + server.getCurrentPlayerCount() + " " + percent + "%")));
    }

    public static void onPlayerLeft(PlayerEvent.PlayerLoggedOutEvent event) {
        EntityPlayer player = event.player;
        if (Main.sleepList.contains(player))
            Main.sleepList.remove(player);
        calculateSpeed(calculatePercent(Main.sleepList.size(), player.getEntityWorld().getMinecraftServer().getCurrentPlayerCount()));
    }

    private static int calculatePercent(int i, int j) {
        return (int) ((i / (double) j) * 100);
    }

    private static int calculateSpeed(int percent) {
        if (percent >= requiem.threshold)
            speed = 10;
        else
            speed = (int) ((percent * 100)  / (double) requiem.threshold);
        return speed;
    }

    public static int getSpeed() {
        return speed;
    }

    private static long calculateAcceleration() {
        return calculateAcceleration(speed);
    }
    private static long calculateAcceleration(int speed) {
        if (speed == 10)
            return -1;
        return (long) (30 * speed);
    }

    public static long getNextTime(long currentTime, long acceleration) {
        if (acceleration == -1)
            return 0;
        return currentTime + acceleration;
    }
    public static long getNextTimeByspeed(long currentTime, int speed) {
        return getNextTime(currentTime, calculateAcceleration(speed));
    }

    public static long getNextTime(long currentTime) {
        return getNextTime(currentTime, calculateAcceleration());
    }

    public static long getTime(long currentTime) {
        if (currentTime < 24000)
            return currentTime;
        currentTime%=24000;
        return currentTime;
    }
}
