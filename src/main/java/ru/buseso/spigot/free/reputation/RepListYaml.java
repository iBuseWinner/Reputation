package ru.buseso.spigot.free.reputation;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class RepListYaml implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        try {
            int i = Reputation.players.getInt("players." + p.getName() + ".reps",0);
            String repp = Reputation.players.getString("players."+p.getName()+".repp","no");
            String repm = Reputation.players.getString("players."+p.getName()+".repm","no");
        } catch (Exception ex) {
            Reputation.players.set("players."+p.getName()+".reps",0);
            Reputation.players.set("players."+p.getName()+".repp","no");
            Reputation.players.set("players."+p.getName()+".repm","no");
        }

        RepPlayer pp = new RepPlayer(p);
        Reputation.rps.add(pp);
    }
}
