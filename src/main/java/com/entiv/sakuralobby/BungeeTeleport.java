package com.entiv.sakuralobby;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class BungeeTeleport extends BukkitRunnable {

    private final Player player;
    private final String server;

    public static final List<String> waitList = new ArrayList<>();

    BungeeTeleport(Player player, String server) {
        this.player = player;
        this.server = server;
    }

    @Override
    public void run() {
        if (waitList.contains(player.getName())) {
            Bungee.getInstance().connect(player, server);
            waitList.remove(player.getName());
        }
    }
}
