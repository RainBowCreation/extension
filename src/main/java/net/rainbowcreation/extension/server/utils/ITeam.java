package net.rainbowcreation.extension.server.utils;

import net.minecraft.server.MinecraftServer;
import net.rainbowcreation.extension.server.Main;


public class ITeam {
    public static void joinTeam(MinecraftServer server , String playername, String team) {
        if (Main.blacklist.contains(playername)) {
            return;
        }
        server.getCommandManager().executeCommand(server ,"scoreboard teams join " + team + " " + playername);
    }
}
