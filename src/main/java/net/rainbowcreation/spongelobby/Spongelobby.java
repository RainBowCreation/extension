package net.rainbowcreation.spongelobby;

import com.google.inject.Inject;
import net.rainbowcreation.spongelobby.bungee.BungeeManager;
import net.rainbowcreation.spongelobby.queue.QManager;
import net.rainbowcreation.spongelobby.queue.Qlist;
import net.rainbowcreation.spongelobby.utils.Reference;
import org.slf4j.Logger;
import org.spongepowered.api.entity.EnderCrystal;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.complex.EnderDragon;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import sun.font.LayoutPathImpl;

import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The main class of your Sponge plugin.
 *
 * <p>All methods are optional -- some common event registrations are included as a jumping-off point.</p>
 */
@Plugin(id = Reference.MODID, name = Reference.MODNAME, version = Reference.MODVERSION)
public class Spongelobby {

    private static PluginContainer container;
    private final Logger logger;
    public Spongelobby instance;

    public List<String> key = new ArrayList<String>();
    private List<Boolean> value = new ArrayList<Boolean>();
    private static List<Boolean> logged = new ArrayList<Boolean>();
    private static String MODE = "lobby";
    private Qlist qlist = new Qlist();

    @Inject
    Spongelobby(final PluginContainer container, final Logger logger) {
        this.container = container;
        this.logger = logger;
        instance = this;
    }

    @Listener
    public void onServerStarting(GameStartedServerEvent event) {
        List<String> header = Arrays.asList("######################################################################################",
                "#  _____       _       ____                 _____                _   _               #",
                "# |  __ \\     (_)     |  _ \\               / ____|              | | (_)              #",
                "# | |__) |__ _ _ _ __ | |_) | _____      _| |     _ __ ___  __ _| |_ _  ___  _  __   #",
                "# |  _  // _` | | '_ \\|  _ < / _ \\ \\ /\\ / / |    | '__/ _ \\/ _`  | __| |/ _ \\| '_ \\  #",
                "# | | \\ \\ (_| | | | | | |_) | (_) \\ V  V /| |____|  | |  __/ (_| | |_| | (_) | | | | #",
                "# |_|  \\_\\__,_|_|_| |_|____/ \\___/ \\_/\\_/  \\_____|_|  \\___|\\__,_|\\__|_|\\___/|_|  |_| #",
                "#                                                                                    #",
                "####################################################################spongelobby#######");
        for (String txt : header) {
            logger.info(txt);
        }
        qlist.add(new QManager("main_vip").priority(9));
        qlist.add(new QManager("main"));
    }

    @Listener
    public void onServerStopping(GameStoppingServerEvent event) {
        this.logger.info("Server Stopped");
    }

    @Listener
    public void onPlayerMove(MoveEntityEvent.Position event, @Root Player player) {
        String name = player.getName();
        if (!key.contains(name))
            return;
        if (value.get(key.indexOf(name)) || logged.get(key.indexOf(name))) {
            return;
        }
        Location<World> location = event.getToTransform().getLocation();
        double x,y,z;
        x = location.getX();
        y = location.getY();
        z = location.getZ();
        switch (MODE) {
            case ("lobby"): {
                if (y < 63 || y > 65)
                    return;
                String server = "";
                if ((x > 39) && (x <= 41) && (z >= -7) && (z <= 5)) {
                    server = "rlcraft_open";
                } else if ((x >= -17) && (x <= -16) && (z >= -33) && (z <= -32)) {
                    server = "rlcraft_test";
                } else if ((x > 13) && (x <= 14)) {
                    if (z >= -28 && z <= -27) {
                        server = "rlcraft_private1"; //bi weekly
                    } else if (z >= -19 && z <= -18) {
                        server = "rlcraft_private2"; //weekly
                    }
                    else
                        return;
                } else
                    return;
                Title title = Title.builder().title(Text.of("You are joining the server")).subtitle(Text.of("Please wait for a moment")).build();
                player.sendTitle(title);
                value.set(key.indexOf(name), true);
                BungeeManager.connect(player, server);
                Task.Builder taskbuilder = Task.builder();
                taskbuilder.execute(task -> {
                    value.set(key.indexOf(name), false);
                }).delay(5, TimeUnit.SECONDS).submit(instance);
                break;
            }
        }
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent event, @Root Player player) {
        String name = player.getName();
        logger.info(name + " JOINED!!!");
        if (!key.contains(name)) {
            key.add(name);
            value.add(true);
            logged.add(false);
        }
        value.set(key.indexOf(name), true);
        value.set(key.indexOf(name), true);
        Task.Builder taskbuilder = Task.builder();
        taskbuilder.execute(task -> {
            value.set(key.indexOf(name), false);
            logged.set(key.indexOf(name), false);
        }).delay(5, TimeUnit.SECONDS).submit(instance);
    }

    @Listener
    public void onStartedServerEvent(GameStartedServerEvent event) {
        BungeeManager.init();
    }

    public static PluginContainer getPlugin() {
        return container;
    }

    @Listener
    //154
}
