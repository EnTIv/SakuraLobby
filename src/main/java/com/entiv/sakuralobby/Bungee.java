package com.entiv.sakuralobby;

import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Bungee {

    private static Bungee bungee;

    private Bungee() {

    }

    public static Bungee getInstance() {
        if (bungee == null) {
            bungee = new Bungee();
        }
        return bungee;
    }

    public void connect(Player player, String server) {
        sendBungeeData(player, "Connect", server);
    }

    private void sendBungeeData(Player player, String... args) {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteArray);

        for (String arg : args) {
            try {
                out.writeUTF(arg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        player.sendPluginMessage(Main.getInstance(), "BungeeCord", byteArray.toByteArray());

    }
}
