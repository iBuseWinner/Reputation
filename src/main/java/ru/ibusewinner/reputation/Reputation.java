package ru.ibusewinner.reputation;

import org.bukkit.plugin.java.JavaPlugin;
import ru.ibusewinner.plugin.buseapi.config.ConfigManager;
import ru.ibusewinner.reputation.commands.AdminReputationCommand;
import ru.ibusewinner.reputation.data.RepMySQL;
import ru.ibusewinner.reputation.data.items.User;
import ru.ibusewinner.reputation.utils.UpdateReputationRunnable;

import java.util.ArrayList;
import java.util.List;

public class Reputation extends JavaPlugin {

    private static Reputation instance;
    private static ConfigManager settings;
    private static RepMySQL mySQL;

    private static List<User> cachedUsers = new ArrayList<>();

    public void onEnable() {
        instance = this;
        settings = new ConfigManager(this, this.getDataFolder(), "config.yml");
        settings.createConfig();
        mySQL = new RepMySQL(settings.getConfig().getString("mysql.host"),
                settings.getConfig().getInt("mysql.port"),
                settings.getConfig().getString("mysql.database"),
                settings.getConfig().getString("mysql.args", "?autoReconnect=true"),
                settings.getConfig().getString("mysql.user"),
                settings.getConfig().getString("mysql.password"));
        mySQL.createTables();

        if (settings.getConfig().getBoolean("settings.auto-update-reputation", false)) {
            new UpdateReputationRunnable().runTaskTimerAsynchronously(this, 20, 20);
        }

        new AdminReputationCommand(this, "adminreputation");
    }

    public void onDisable() {
        for (User user : Reputation.getCachedUsers()) {
            Reputation.getMySQL().updateUser(user);
        }
    }

    public static User getCachedUser(String nickname) {
        for (User user : getCachedUsers()) {
            if (user.getNickname().equals(nickname)) {
                return user;
            }
        }
        return null;
    }

    public static Reputation getInstance() {
        return instance;
    }

    public static ConfigManager getSettings() {
        return settings;
    }

    public static List<User> getCachedUsers() {
        return cachedUsers;
    }

    public static RepMySQL getMySQL() {
        return mySQL;
    }
}
