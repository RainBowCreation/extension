package net.rainbowcreation.extension.server.utils;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.util.text.ITextComponent;
import net.rainbowcreation.extension.server.Main;

public class IPacket {
    public static void sent(EntityPlayerMP player, ITextComponent textComponent, SPacketTitle.Type type, int fadein, int duration, int fadeout) {
        if (Main.blacklist.contains(player.getName())) {
            player.sendMessage(textComponent);
        }
        else {
            SPacketTitle packetTitle = new SPacketTitle(type, textComponent, fadein, duration, fadeout);
            player.connection.sendPacket(packetTitle);
        }
    }
}