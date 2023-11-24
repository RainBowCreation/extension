package net.rainbowcreation.spongelobby.bungee;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.Predicate;

import net.rainbowcreation.spongelobby.Spongelobby;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.ChannelRegistrar;
import org.spongepowered.api.network.RawDataListener;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

public class BungeeManager {
    private static DataListener listener = new DataListener();

    public static void init() {
        Sponge.getChannelRegistrar().getOrCreateRaw(Spongelobby.getPlugin(), "BungeeCord").addListener(Platform.Type.SERVER, listener);
    }

    private static ChannelBinding.RawDataChannel getChannel() {
        ChannelBinding.RawDataChannel channel;
        ChannelRegistrar channelRegistrar = Sponge.getChannelRegistrar();
        if (!channelRegistrar.getChannel("BungeeCord").isPresent()) {
            channel = channelRegistrar.getOrCreateRaw(Spongelobby.getPlugin(), "BungeeCord");
            channel.addListener(Platform.Type.SERVER, listener);
        } else {
            channel = channelRegistrar.getOrCreateRaw(Spongelobby.getPlugin(), "BungeeCord");
        }
        return channel;
    }

    public static void connect(Player player, String server) {
        getChannel().sendTo(player, buffer -> buffer.writeUTF("Connect").writeUTF(server));
    }

    public static void connectOther(String player, String server, Player reference) {
        getChannel().sendTo(reference, buffer -> buffer.writeUTF("ConnectOther").writeUTF(player).writeUTF(server));
    }

    public static void kickPlayer(String player, Text reason, Player reference) {
        getChannel().sendTo(reference, buffer -> buffer.writeUTF("KickPlayer").writeUTF(player).writeUTF(TextSerializers.FORMATTING_CODE.serialize(reason)));
    }

    public static void message(String player, Text message, Player reference) {
        getChannel().sendTo(reference, buffer -> buffer.writeUTF("Message").writeUTF(player).writeUTF(TextSerializers.FORMATTING_CODE.serialize(message)));
    }

    public static void playerCount(String server, IntConsumer consumer, Player reference) {
        getChannel().sendTo(reference, buffer -> buffer.writeUTF("PlayerCount").writeUTF(server));
        listener.map.put(buffer -> (buffer.resetRead().readUTF().equals("PlayerCount") && buffer.readUTF().equals(server)), buffer -> consumer.accept(buffer.readInteger()));
    }
    /*

    public static void playerList(String server, Consumer<List<String>> consumer, Player reference) {
        getChannel().sendTo(reference, buffer -> buffer.writeUTF("PlayerList").writeUTF(server));
        listener.map.put(buffer -> (buffer.resetRead().readUTF().equals("PlayerList") && buffer.readUTF().equals(server)), buffer -> consumer.accept(ImmutableList.builder().add((Object[])buffer.readUTF().split(", ")).build()));
    }*/

    public static void ip(Player player, Consumer<InetSocketAddress> consumer) {
        getChannel().sendTo(player, buffer -> buffer.writeUTF("IP"));
        listener.map.put(buffer -> buffer.resetRead().readUTF().equals("IP"), buffer -> consumer.accept(new InetSocketAddress(buffer.readUTF(), buffer.readInteger())));
    }/*

    public static void getServers(Consumer<List<String>> consumer, Player reference) {
        getChannel().sendTo(reference, buffer -> buffer.writeUTF("GetServers"));
        listener.map.put(buffer -> buffer.resetRead().readUTF().equals("GetServers"), buffer -> consumer.accept(ImmutableList.builder().add((Object[])buffer.readUTF().split(", ")).build()));
    }*/

    public static void getServer(Consumer<String> consumer, Player reference) {
        getChannel().sendTo(reference, buffer -> buffer.writeUTF("GetServer"));
        listener.map.put(buffer -> buffer.resetRead().readUTF().equals("GetServer"), buffer -> consumer.accept(buffer.readUTF()));
    }

    public static void serverIP(String server, Consumer<InetSocketAddress> consumer, Player reference) {
        getChannel().sendTo(reference, buffer -> buffer.writeUTF("ServerIP").writeUTF(server));
        listener.map.put(buffer -> (buffer.resetRead().readUTF().equals("ServerIP") && buffer.readUTF().equals(server)), buffer -> consumer.accept(new InetSocketAddress(buffer.readUTF(), buffer.readShort())));
    }

    public static class DataListener implements RawDataListener {
        ConcurrentMap<Predicate<ChannelBuf>, Consumer<ChannelBuf>> map = Maps.newConcurrentMap();

        public void handlePayload(ChannelBuf buffer, RemoteConnection connection, Platform.Type type) {
            for (Map.Entry<Predicate<ChannelBuf>, Consumer<ChannelBuf>> entry : this.map.entrySet()) {
                if (((Predicate<ChannelBuf>)entry.getKey()).test(buffer)) {
                    ((Consumer<ChannelBuf>)entry.getValue()).accept(buffer);
                    this.map.remove(entry.getKey());
                    return;
                }
            }
        }
    }
}
