package ru.buseso.spigot.free.reputation.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.buseso.spigot.free.reputation.Reputation;
import ru.buseso.spigot.free.reputation.Utils.RepPlayer;
import ru.buseso.spigot.free.reputation.Utils.RepSender;

public class RepListMySQL implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        RepPlayer pp = Reputation.requests.getPlayer(p);
        if(pp.getUuid().equals("*@%")) {
            Reputation.requests.addPlayer(p);
            pp = Reputation.requests.getPlayer(p);
        }

        Reputation.rps.add(pp);
    }
}
