package net.rainbowcreation.spongelobby.queue;

import net.rainbowcreation.spongelobby.bungee.BungeeManager;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.List;

public class QManager {
    private String name;
    private List<Player> queue = new ArrayList<>();
    private int priority = 10; //default 10 larger lesser priority will go first [0, 19]
    public QManager(String name) {
        this.name = name;
    }

    public QManager priority(int priority) {
        this.priority = priority;
        return this;
    }

    public QManager add(Player player) {
        if (!queue.contains(player))
            queue.add(player);
        return this;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isempty() {
        return queue.isEmpty();
    }

    public List<Player> get() {
        return queue;
    }

    public void execute() {
        Player player = queue.remove(0);
        BungeeManager.connect(player, name);
    }
}
