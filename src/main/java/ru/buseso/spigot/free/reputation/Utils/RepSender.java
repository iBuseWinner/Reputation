package ru.buseso.spigot.free.reputation.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.buseso.spigot.free.reputation.Reputation;

public class RepSender {
    public static void sendToPlayer(Player p, String message) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&',message));
    }

    public static void log(String msg) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', Reputation.config.prefix()+" &7"+msg));
    }

    public static void send(CommandSender s, String msg) {
        s.sendMessage(ChatColor.translateAlternateColorCodes('&',msg));
    }
}
