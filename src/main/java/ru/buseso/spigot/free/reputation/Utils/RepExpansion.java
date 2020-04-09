package ru.buseso.spigot.free.reputation.Utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import ru.buseso.spigot.free.reputation.Reputation;

import java.util.List;

public class RepExpansion extends PlaceholderExpansion {
    @Override
    public String getIdentifier() {
        return "rep";
    }

    @Override
    public String getAuthor() {
        return "BuseSo";
    }

    @Override
    public String getVersion() {
        return Reputation.ins.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
        if(p == null) {
            return "";
        }

        //%rep_rep%
        if(identifier.equals("rep")) {
            return Reputation.getRepPlayerByNick(p.getName()).getReps()+"";
        }

        if(identifier.equals("repp")) {
            return Reputation.getRepPlayerByNick(p.getName()).getRepp();
        }

        if(identifier.equals("repm")) {
            return Reputation.getRepPlayerByNick(p.getName()).getRepm();
        }

        if(identifier.startsWith("top_")) {
            List<RepTop> rt;
            if (Reputation.config.dataType().equalsIgnoreCase("mysql")) rt = Reputation.requests.getTopPlayers();
            else rt = Reputation.getTopPlayers();
            for (int i = 1; i <= Reputation.config.topLimit(); i++) {
                if (identifier.equals("top_" + i + "_nick")) {
                    return rt.get(i - 1).name;
                }

                if (identifier.equals("top_" + i + "_rep")) {
                    return "" + rt.get(i - 1).reps;
                }
            }
        }

        return null;
    }
}
