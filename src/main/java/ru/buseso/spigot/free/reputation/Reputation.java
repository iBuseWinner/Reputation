package ru.buseso.spigot.free.reputation;

/*
 ███████╗██████╗ ██╗ ██████╗  ██████╗ ████████╗███╗   ███╗ ██████╗   ██████╗ ██╗   ██╗
 ██╔════╝██╔══██╗██║██╔════╝ ██╔═══██╗╚══██╔══╝████╗ ████║██╔════╝   ██╔══██╗██║   ██║
 ███████╗██████╔╝██║██║  ███╗██║   ██║   ██║   ██╔████╔██║██║        ██████╔╝██║   ██║
 ╚════██║██╔═══╝ ██║██║   ██║██║   ██║   ██║   ██║╚██╔╝██║██║        ██╔══██╗██║   ██║
 ███████║██║     ██║╚██████╔╝╚██████╔╝   ██║   ██║ ╚═╝ ██║╚██████╗██╗██║  ██║╚██████╔╝
 ╚══════╝╚═╝     ╚═╝ ╚═════╝  ╚═════╝    ╚═╝   ╚═╝     ╚═╝ ╚═════╝╚═╝╚═╝  ╚═╝ ╚═════╝

    SpigotMc.Ru plugin link: https://spigotmc.ru/resources/124/
    SpigotMc.Org plugin link: soon...

    With love by BuseSo
    С любовью от BuseSo

    2020
 */

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.buseso.spigot.free.reputation.updates.UpdateException;
import ru.buseso.spigot.free.reputation.updates.UpdateResult;
import ru.buseso.spigot.free.reputation.updates.Updater;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Reputation extends JavaPlugin {

    static RepConfig config;
    static RepMySQL mysql;
    static RepMySQL.Requests requests;
    static RepTimer timer;
    static RepYamlTimer ytimer;
    static List<RepPlayer> rps;
    static Reputation ins;
    static FileConfiguration players;

    @Override
    public void onEnable() {
        ins = this;

        saveDefaultConfig();
        config = new RepConfig(getConfig());
        RepSender.log("&aConfig enabled!");
        rps = new ArrayList<>();

        if(config.checkUpdates()) checkUpdate();

        if(config.autoSave() < 0) {
            RepSender.log("&4Auto-save player information is turned off. I don't recommend using this mode.");
        }

        RepSender.log("Continue to run the plugin...");
        if(config.dataType().equalsIgnoreCase("mysql")) {
            RepSender.log("&aConnecting to MySQL...");
            mysql = new RepMySQL(config.sqlHost(), config.sqlPort(),
                    config.sqlDatabase(),config.sqlUser(),
                    config.sqlPassword(),config.sqlTable());
            requests = new RepMySQL.Requests();

            mysql.connect();
            RepSender.log("&aCreating table if not exists...");
            requests.createTable();

            if(config.autoSave() == 0) {
                RepSender.log("&aAuto-save player information: quit mode.");
            } else if(config.autoSave() > 0) {
                RepSender.log("&aAuto-save player information: timer mode.");

                timer = new RepTimer();
                timer.runTaskTimerAsynchronously(this, 0, 20*config.autoSave());
            }

            Bukkit.getPluginManager().registerEvents(new RepListMySQL(), this);
            if(config.autoSave() >= 0) {
                Bukkit.getPluginManager().registerEvents(new RepListenerMySQL(), this);
            }
        } else {
            RepSender.log("&aConnecting to yaml...");
            createYaml();

            if(config.autoSave() == 0) {
                RepSender.log("&aAuto-save player information: quit mode.");
            } else if(config.autoSave() > 0) {
                RepSender.log("&aAuto-save player information: timer mode.");

                ytimer = new RepYamlTimer();
                ytimer.runTaskTimerAsynchronously(this, 0, 20*config.autoSave());
            }

            Bukkit.getPluginManager().registerEvents(new RepListYaml(), this);
            if(config.autoSave() >= 0) {
                Bukkit.getPluginManager().registerEvents(new RepListenerYaml(), this);
            }
        }

        RepSender.log("Trying to find PlaceholderAPI...");
        try {
            if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                new RepExpansion().register();
                RepSender.log("PlaceholderAPI found!");
            }
        }catch (NullPointerException ignored) { }

        Bukkit.getPluginCommand("reputation").setExecutor(new RepCmd());

        RepSender.log("&aPlugin successfully enabled!");
    }

    @Override
    public void onDisable() {
        if(config.dataType().equalsIgnoreCase("mysql")) {
            if(config.autoSave() > 0) {
                timer.cancel();
            }
            mysql.disconnect();
        } else {
            if(config.autoSave() > 0) {
                ytimer.cancel();
            }
            try {
                for(RepPlayer pp : rps) {
                    players.set("players."+pp.getUuid()+".reps",pp.getReps());
                    players.set("players."+pp.getUuid()+".repp",pp.getRepp());
                    players.set("players."+pp.getUuid()+".repm",pp.getRepm());
                }
                players.save(new File(getDataFolder(), "players.yml"));
            } catch (IOException ex) {
                if(config.debugMode()) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static int getVersion() { return config.version(); }

    private void checkUpdate() {
        Updater updater = new Updater("iBuseWinner","Reputation");
        new Thread(() -> {
            try {
                UpdateResult result = updater.checkUpdates();
                if(result.hasUpdates()) {
                    RepSender.log("&aFound an update! Please download the latest version!");
                } else {
                    RepSender.log("&cNo updates found! You are using the latest version!");
                }
            }catch (UpdateException ignored) { }
        }).start();
    }

    private void createYaml() {
        File file = new File(getDataFolder(), "players.yml");

        if(!file.exists()) {
            if(config.debugMode()) {
                RepSender.log("&cCreating file players.yml because it doesn't exist.");
            }
            file.getParentFile().mkdirs();
            saveResource("players.yml",false);
        }

        players = new YamlConfiguration();
        try {
            players.load(file);
        }catch (IOException | InvalidConfigurationException e) {
            if(config.debugMode()) {
                e.printStackTrace();
            }
        }
    }

    public static String getRepsByNick(String nick) {
        for(RepPlayer pp : rps) {
            if(pp.getUuid().equals(nick)) {
                return ""+pp.getReps();
            }
        }
        return "notfound";
    }

    public static RepPlayer getOnlineRepPlayerByNick(String nick) {
        for(RepPlayer pp : rps) {
            if(pp.getUuid().equals(nick)) {
                return pp;
            }
        }
        return null;
    }

    public static RepPlayer getOfflineRepPlayerByNick(String nick) {
        if(config.dataType().equalsIgnoreCase("yaml")) {
            return new RepPlayer(nick);
        } else if(config.dataType().equalsIgnoreCase("mysql")) {
            return requests.getPlayer(nick);
        }
        return null;
    }

    public static void reloadCfg() {
        Reputation.config = null;
        try {
            Reputation.ins.getConfig().load(new File(Reputation.ins.getDataFolder(), "config.yml"));
            Reputation.config = new RepConfig(Reputation.ins.getConfig());
        }catch (Exception ex) {
            Reputation.ins.saveDefaultConfig();
        }
    }

    public static RepPlayer getRepPlayerByNick(String nick) {
        if(getOnlineRepPlayerByNick(nick) == null) {
            if(getOfflineRepPlayerByNick(nick) == null) {
                return null;
            } else {
                return getOfflineRepPlayerByNick(nick);
            }
        } else {
            return getOnlineRepPlayerByNick(nick);
        }
    }
}
