package ru.buseso.spigot.free.reputation.Utils;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class RepCooldown extends BukkitRunnable {
    public HashMap<UUID, Long> cd = new HashMap<>();

    @Override
    public void run() {
        for(UUID uuid : cd.keySet()) {
            long time = cd.get(uuid)-1;

            if(time > 0) {
                cd.put(uuid, time);
                Bukkit.getPlayer(uuid).sendMessage("Cd: "+time);
            } else {
                cd.remove(uuid);
            }
        }
    }
}
