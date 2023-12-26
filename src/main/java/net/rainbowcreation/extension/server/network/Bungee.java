package net.rainbowcreation.extension.server.network;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.util.text.TextComponentString;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Bungee {
    // Method to send the player to a specific server
    public static void sendPlayerToServer(EntityPlayerMP player, String serverName) {
        // Create a ByteBuf with the server name
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            // Write the "Connect" message and server name to the stream
            out.writeUTF("Connect");
            out.writeUTF(serverName);

            // Convert the ByteArrayOutputStream to a byte array
            byte[] data = b.toByteArray();

            // Create a custom payload packet with the byte array
            SPacketCustomPayload packet = new SPacketCustomPayload("BungeeCord", new PacketBuffer(Unpooled.wrappedBuffer(data)));

            // Send the packet to the player
            player.sendMessage(new TextComponentString("Send"));
            player.connection.sendPacket(packet);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close the streams
            try {
                b.close();
                out.close();
                player.sendMessage(new TextComponentString("Sent!"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}