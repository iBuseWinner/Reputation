package ru.buseso.spigot.free.reputation.Utils;

import org.bukkit.scheduler.BukkitRunnable;
import ru.buseso.spigot.free.reputation.Reputation;

public class RepTimer extends BukkitRunnable {
    @Override
    public void run() {
        for(RepPlayer pp : Reputation.rps) {
            Reputation.requests.setPlayer(pp);
        }
    }
}
