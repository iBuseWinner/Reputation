package ru.fennec.free.reputation.common.abstracts;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCommand implements CommandExecutor, TabCompleter {

    private Plugin plugin;

    public AbstractCommand(Plugin plugin, String name) {
        this.plugin = plugin;
        PluginCommand pluginCommand = Bukkit.getPluginCommand(name);
        if (pluginCommand != null) {
            pluginCommand.setExecutor(this);
            pluginCommand.setTabCompleter(this);
        }
    }

    public abstract void execute(CommandSender commandSender, String label, String[] args);

    public List<String> complete(CommandSender commandSender, String[] args) {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command,
                             String label, String[] args) {
        execute(commandSender, label, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender,
                                      Command command, String label, String[] args) {
        return filter(complete(commandSender, args), args);
    }

    private List<String> filter(List<String> list, String[] args) {
        if (list == null) {
            return null;
        }
        String last = args[args.length - 1];
        List<String> r = new ArrayList<>();
        for (String s : list) {
            if (s.toLowerCase().startsWith(last.toLowerCase())) {
                r.add(s);
            }
        }
        return r;
    }

    public Plugin getPlugin() {
        return plugin;
    }

}
