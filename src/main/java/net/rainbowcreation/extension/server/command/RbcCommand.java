package net.rainbowcreation.extension.server.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.rainbowcreation.extension.server.Main;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import scala.Array;
import scala.actors.threadpool.Arrays;

import java.util.ArrayList;
import java.util.List;

public class RbcCommand implements ICommand {
    private final List<String> aliases = Arrays.asList(new String[] {"Rbc", "RBC", "rainbowcreation", "RainBowCreation", "Rainbowcreation"});

    @Override
    public String getName() {
        return "rbc";
    }

    @Override
    public String getUsage(ICommandSender iCommandSender) {
        return "/rbc to open main gui";
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(MinecraftServer minecraftServer, ICommandSender iCommandSender, String[] strings) throws CommandException {
        EntityPlayer player = null;
        if (iCommandSender instanceof EntityPlayer)
            player = (EntityPlayer) iCommandSender;
        if (strings.length == 0) {
            if (player != null) {
                player.openGui(Main.getInstance(), 0, player.getEntityWorld(), (int) player.posX, (int) player.posY, (int) player.posZ);
            }
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer minecraftServer, ICommandSender iCommandSender) {
        return false;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer minecraftServer, ICommandSender iCommandSender, String[] strings, @Nullable BlockPos blockPos) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] strings, int i) {
        return false;
    }

    @Override
    public int compareTo(@NotNull ICommand o) {
        return 0;
    }
}
