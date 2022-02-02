package ru.ibusewinner.reputation.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.ibusewinner.reputation.Reputation;
import ru.ibusewinner.reputation.data.items.User;

import java.util.ArrayList;

public class ConnectionListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = Reputation.getMySQL().getUserByNickname(player.getName());
        if (user == null) {
            user = new User(-1, player.getName(), 0, new ArrayList<>());
            Reputation.getMySQL().createUser(user);
        }
        Reputation.getCachedUsers().add(user);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User user = Reputation.getCachedUser(player.getName());
        if (user != null) {
            Reputation.getMySQL().updateUser(user);
            Reputation.getCachedUsers().remove(user);
        }
    }

}
