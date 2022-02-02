package ru.ibusewinner.reputation.utils;

import org.bukkit.scheduler.BukkitRunnable;
import ru.ibusewinner.reputation.Reputation;
import ru.ibusewinner.reputation.data.items.User;

public class UpdateReputationRunnable extends BukkitRunnable {
    @Override
    public void run() {
        for (User user : Reputation.getCachedUsers()) {
            Reputation.getMySQL().updateUser(user);
        }
    }
}
