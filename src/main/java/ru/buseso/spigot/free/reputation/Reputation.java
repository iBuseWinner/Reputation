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

    2020-2021
 */

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.buseso.spigot.free.reputation.Data.RepConfig;
import ru.buseso.spigot.free.reputation.Data.RepMySQL;
import ru.buseso.spigot.free.reputation.Listeners.RepListMySQL;
import ru.buseso.spigot.free.reputation.Listeners.RepListYaml;
import ru.buseso.spigot.free.reputation.Listeners.RepListenerMySQL;
import ru.buseso.spigot.free.reputation.Listeners.RepListenerYaml;
import ru.buseso.spigot.free.reputation.Utils.*;
import ru.buseso.spigot.free.reputation.updates.UpdateException;
import ru.buseso.spigot.free.reputation.updates.UpdateResult;
import ru.buseso.spigot.free.reputation.updates.Updater;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public final class Reputation extends JavaPlugin {

    public static RepConfig config;
    private static RepMySQL mysql;
    public static RepMySQL.Requests requests;
    private static RepTimer timer;
    private static RepYamlTimer ytimer;
    public static List<RepPlayer> rps;
    public static Reputation ins;
    public static FileConfiguration players;
    public static RepCooldown repCD;

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

        repCD = new RepCooldown();
        repCD.runTaskTimerAsynchronously(this, 0, 20);

        Bukkit.getPluginCommand("reputation").setExecutor(new RepCmd());

        RepSender.log("&aPlugin successfully enabled!");
    }

    @Override
    public void onDisable() {
        if(config.dataType().equalsIgnoreCase("mysql")) {
            if(config.autoSave() > 0) {
                try {
                    timer.cancel();
                } catch (NullPointerException e) {
                    if(config.debugMode()) {
                        e.printStackTrace();
                    }
                }
            }
            mysql.disconnect();
        } else {
            if(config.autoSave() > 0) {
                try {
                    ytimer.cancel();
                } catch (NullPointerException e) {
                    if(config.debugMode()) {
                        e.printStackTrace();
                    }
                }
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
        repCD.cancel();

        RepSender.log("&cPlugin successfully disabled!");
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

    public static List<RepTop> getTopPlayers() {
        List<RepTop> list = new ArrayList<>();
        HashMap<String, Integer> allPlayers = new HashMap<>();

        Set<String> keys = null;
        try {
            keys = players.getConfigurationSection("players").getKeys(false);
        } catch (NullPointerException e) {
            if(config.debugMode()) {
                e.printStackTrace();
            }
        }
        for(String s : keys) {
            int reps = players.getInt("players."+s+".reps");
            allPlayers.put(s,reps);
        }

        Map<String, Integer> sorted = allPlayers.entrySet().stream()
                .sorted(Map.Entry.<String,Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        List<RepTop> nedo = new ArrayList<>();
        for (String s : sorted.keySet()) {
            RepTop rt = new RepTop(s, sorted.get(s));
            nedo.add(rt);
        }

        int i = 0;
        while (i < nedo.size() && i < config.topLimit()) {
            list.add(nedo.get(i));
            i++;
        }
//        for(int i = 0; i < config.topLimit(); i++) {
//            if(i < sorted.size()) {
//                Bukkit.broadcastMessage("i < sorted.size()");
//                for(String s : sorted.keySet()) {
//                    RepTop rt = new RepTop(s, sorted.get(s));
//                    list.add(rt);
//                }
//            }else break;
//        }
        return list;
    }
}
