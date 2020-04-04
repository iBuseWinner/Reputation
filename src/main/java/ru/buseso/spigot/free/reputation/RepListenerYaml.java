package ru.buseso.spigot.free.reputation;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;

public class RepListenerYaml implements Listener {
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        RepPlayer pp = Reputation.getOnlineRepPlayerByNick(p.getName());
        Reputation.players.set("players."+pp.getUuid()+".reps",pp.getReps());
        Reputation.players.set("players."+pp.getUuid()+".repp",pp.getRepp());
        Reputation.players.set("players."+pp.getUuid()+".repm",pp.getRepm());

        Bukkit.getScheduler().callSyncMethod(Reputation.ins, () -> Reputation.rps.remove(pp));

        try {
            Reputation.players.save(new File(Reputation.ins.getDataFolder(), "players.yml"));
        } catch (IOException ex) {
            if(Reputation.config.debugMode()) {
                ex.printStackTrace();
            }
        }
    }
}
