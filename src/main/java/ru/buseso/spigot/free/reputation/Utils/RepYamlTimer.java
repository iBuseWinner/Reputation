package ru.buseso.spigot.free.reputation.Utils;

import org.bukkit.scheduler.BukkitRunnable;
import ru.buseso.spigot.free.reputation.Reputation;

import java.io.File;
import java.io.IOException;

public class RepYamlTimer extends BukkitRunnable {
    @Override
    public void run() {
        for(RepPlayer pp : Reputation.rps) {
            Reputation.players.set("players."+pp.getUuid()+".reps",pp.getReps());
            Reputation.players.set("players."+pp.getUuid()+".repp",pp.getRepp());
            Reputation.players.set("players."+pp.getUuid()+".repm",pp.getRepm());
        }

        try {
            Reputation.players.save(new File(Reputation.ins.getDataFolder(), "players.yml"));
        }catch (IOException ex) {
            if(Reputation.config.debugMode()) {
                ex.printStackTrace();
            }
        }
    }
}
