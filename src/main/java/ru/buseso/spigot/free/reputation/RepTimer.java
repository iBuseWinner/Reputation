package ru.buseso.spigot.free.reputation;

import org.bukkit.scheduler.BukkitRunnable;

public class RepTimer extends BukkitRunnable {
    @Override
    public void run() {
        for(RepPlayer pp : Reputation.rps) {
            Reputation.requests.setPlayer(pp);
        }
    }
}
