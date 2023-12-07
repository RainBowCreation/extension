package net.rainbowcreation.extension.server.utils;

import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;
import java.util.List;

public class Reference {
    public static final String MODID = "rbcextension";
    public static final String NAME = "RBCextension";
    public static final String VERSION = "1.12.2-v1.4-server-winter";

    public static final List<String> HEADER = Arrays.asList("######################################################################################",
            "#  _____       _       ____                 _____                _   _               #",
            "# |  __ \\     (_)     |  _ \\               / ____|              | | (_)              #",
            "# | |__) |__ _ _ _ __ | |_) | _____      _| |     _ __ ___  __ _| |_ _  ___  _  __   #",
            "# |  _  // _` | | '_ \\|  _ < / _ \\ \\ /\\ / / |    | '__/ _ \\/ _`  | __| |/ _ \\| '_ \\  #",
            "# | | \\ \\ (_| | | | | | |_) | (_) \\ V  V /| |____|  | |  __/ (_| | |_| | (_) | | | | #",
            "# |_|  \\_\\__,_|_|_| |_|____/ \\___/ \\_/\\_/  \\_____|_|  \\___|\\__,_|\\__|_|\\___/|_|  |_| #",
            "#                                                                                    #"); //long = 86

    public static final SPacketTitle PACKET_TITLE = new SPacketTitle(SPacketTitle.Type.TITLE, new TextComponentString("Welcome to") , 20, 120, 20);
    public static final SPacketTitle PACKET_SUB_TITLE = new SPacketTitle(SPacketTitle.Type.SUBTITLE, new TextComponentString("rl." + TextFormatting.GREEN + "R" + TextFormatting.AQUA + "a" + TextFormatting.LIGHT_PURPLE + "i" + TextFormatting.RED + "n" + TextFormatting.YELLOW + "B" + TextFormatting.GREEN + "o" + TextFormatting.AQUA + "w" + TextFormatting.RESET + "Creation.net"), 20, 120, 20);
    public static final SPacketTitle PACKET_MAINTENANCE_COMPLETE = new SPacketTitle(SPacketTitle.Type.TITLE, new TextComponentString(TextFormatting.RED + "Daily Maintenance " + TextFormatting.GREEN + "Completed!!") , 10, 200, 10);
}
