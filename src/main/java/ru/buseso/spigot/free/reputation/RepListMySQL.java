package ru.buseso.spigot.free.reputation;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class RepListMySQL implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        RepPlayer pp = Reputation.requests.getPlayer(p);
        if(pp == null) {
            Reputation.requests.addPlayer(p);
        }

        Reputation.rps.add(pp);
    }
}
