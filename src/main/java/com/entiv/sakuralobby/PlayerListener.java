package com.entiv.sakuralobby;

import fr.xephi.authme.events.LoginEvent;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class PlayerListener implements Listener {

    Main plugin = Main.getInstance();


    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (plugin.getConfig().getBoolean("基础设定.建筑保护") || !event.getPlayer().isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (plugin.getConfig().getBoolean("基础设定.建筑保护") || !event.getPlayer().isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (plugin.getConfig().getBoolean("基础设定.饥饿保护")) {
            event.setFoodLevel(20);
        }
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (plugin.getConfig().getBoolean("基础设定.清理屏幕")) {
            for (byte b1 = 0, b2 = 5; b1 < b2; b1++) {
                event.getPlayer().sendMessage(" \n \n \n \n \n \n \n \n \n \n \n");
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && plugin.getConfig().getBoolean("基础设定.伤害保护")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerFall(PlayerMoveEvent event) {

        int checkY = plugin.getConfig().getInt("虚空保护.检测Y轴");
        if (checkY <= -1) return;

        Player player = event.getPlayer();
        if (checkY >= player.getLocation().getY()) {

            double x = plugin.getConfig().getDouble("虚空保护.传送地点.x");
            double y = plugin.getConfig().getDouble("虚空保护.传送地点.y");
            double z = plugin.getConfig().getDouble("虚空保护.传送地点.z");

            float yaw = plugin.getConfig().getInt("虚空保护.传送地点.yaw");
            float pitch = plugin.getConfig().getInt("虚空保护.传送地点.pitch");

            Location location = new Location(player.getWorld(), x, y, z, yaw, pitch);
            player.teleport(location);
        }
    }

    @EventHandler
    public void onLoginEvent(LoginEvent event) {
        Player player = event.getPlayer();
        int delay = plugin.getConfig().getInt("跨服传送.延迟秒数");

        if (plugin.getConfig().getBoolean("跨服传送.开启")) {
            String server = plugin.getConfig().getString("跨服传送.服务器名");

            BungeeTeleport.waitList.add(player.getName());

            BungeeTeleport bungeeTeleport = new BungeeTeleport(player, server);
            bungeeTeleport.runTaskLaterAsynchronously(Main.getInstance(), delay * 20L);

            sendTeleportMessage(player, delay);
        }

        runLoginCommand(player);
    }

    private void runLoginCommand(Player player) {
        int delay = plugin.getConfig().getInt("跨服传送.延迟秒数");
        List<String> commands = plugin.getConfig().getStringList("登录指令");

        Message.replace(commands, "%delay%", String.valueOf(delay), "%player%", player.getName());
        Message.toColor(commands);

        Server server = Main.getInstance().getServer();

        for (String command : commands) {
            server.dispatchCommand(server.getConsoleSender(), command);
        }

        Message.sendConsole(commands);
    }

    private void sendTeleportMessage(Player player, int delay) {
        String tpMessage = plugin.getConfig().getString("跨服传送.传送消息");
        if (tpMessage == null) return;

        new BukkitRunnable() {
            int time = delay;

            @Override
            public void run() {
                Message.sendActionBar(player, tpMessage.replace("%delay%", String.valueOf(time--)));
                boolean playerCancel = !BungeeTeleport.waitList.contains(player.getName());
                if (time == 0 || playerCancel) cancel();
            }
        }.runTaskTimer(Main.getInstance(), 0, 20L);
    }
}
