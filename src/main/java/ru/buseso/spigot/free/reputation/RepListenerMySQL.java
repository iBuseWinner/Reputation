package ru.buseso.spigot.free.reputation;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class RepListenerMySQL implements Listener {
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        RepPlayer pp = Reputation.getOnlineRepPlayerByNick(p.getName());
        Reputation.requests.setPlayer(pp);
        Bukkit.getScheduler().callSyncMethod(Reputation.ins, () -> Reputation.rps.remove(pp));
    }
}
