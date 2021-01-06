package com.entiv.sakuralobby;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MainCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 1) {

            String subCommand = args[0];

            if (subCommand.equals("reload") && sender.isOp()) {
                Main.getInstance().reloadConfig();
                Message.send(sender, "&9&l" + Main.getInstance().getName() + "&6&l >> &a插件重载成功!");
                return true;
            }

            if (sender instanceof Player) {

                Player player = (Player) sender;

                if (subCommand.equals("cancel")) {
                    BungeeTeleport.waitList.remove(player.getName());
                    return true;
                }

                if (subCommand.equals("teleport")) {
                    FileConfiguration config = Main.getInstance().getConfig();
                    String server = config.getString("跨服传送.服务器名");
                    Bungee.getInstance().connect(player, server);
                    return true;
                }

            }
        }

        List<String> message = new ArrayList<>();

        message.add("&6&m+------------------+&9&l " + Main.getInstance().getName() + " &6&m+------------------+");
        message.add("");
        message.add("&d/lobby teleport &e立即传送至子服");
        message.add("&d/lobby cancel &e留在登录服");
        message.add("&d/lobby reload &e重载插件配置");
        message.add("");

        Message.send(sender, message);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {

            List<String> tabComplete = new ArrayList<>();

            tabComplete.add("reload");
            tabComplete.add("teleport");
            tabComplete.add("cancel");

            tabComplete.removeIf(s -> !s.startsWith(args[0].toLowerCase()));

            return tabComplete;

        }
        return null;
    }
}
