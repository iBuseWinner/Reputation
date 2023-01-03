package ru.fennec.free.reputation.handlers.messages;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.fennec.free.reputation.common.interfaces.IDatabase;
import ru.fennec.free.reputation.common.interfaces.IGamePlayer;
import ru.fennec.free.reputation.handlers.players.PlayersContainer;

public class PlaceholderHook extends PlaceholderExpansion {

    private final String version;
    private final PlayersContainer playersContainer;
    private final IDatabase database;

    public PlaceholderHook(String version, PlayersContainer playersContainer, IDatabase database) {
        this.version = version;
        this.playersContainer = playersContainer;
        this.database = database;
    }

    @Override
    public String getIdentifier() {
        return "reputation";
    }

    @Override
    public String getAuthor() {
        return "BuseSo";
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        IGamePlayer gamePlayer = playersContainer.getCachedPlayerByUUID(player.getUniqueId());
        if (gamePlayer != null) {
            switch (params.toLowerCase()) {
                case "reputation":
                    return String.valueOf(gamePlayer.getPlayerReputation());
                case "id":
                    return String.valueOf(gamePlayer.getId());
                case "favorites_amount":
                    return String.valueOf(gamePlayer.getIDsWhomGaveReputation().size());
                default:
                    try {
                        if (params.toLowerCase().startsWith("top_online_")) {
                            if (params.toLowerCase().endsWith("_name")) {
                                int place = Integer.parseInt(params.toLowerCase()
                                        .replace("top_online_", "")
                                        .replace("_name", ""));
                                return playersContainer.getTopGamePlayerByReputation(place - 1).getBukkitPlayer().getName();
                            } else if (params.toLowerCase().endsWith("_reputation")) {
                                int place = Integer.parseInt(params.toLowerCase()
                                        .replace("top_online_", "")
                                        .replace("_reputation", ""));
                                return String.valueOf(playersContainer.getTopGamePlayerByReputation(place - 1).getPlayerReputation());
                            }
                        } else if (params.toLowerCase().startsWith("top_")) {
                            if (params.toLowerCase().endsWith("_name")) {
                                int place = Integer.parseInt(params.toLowerCase()
                                        .replace("top_", "")
                                        .replace("_name", ""));
                                return Bukkit.getOfflinePlayer(database.getTopGamePlayerUUIDByReputation(place - 1)).getName();
                            } else if (params.toLowerCase().endsWith("_reputation")) {
                                int place = Integer.parseInt(params.toLowerCase()
                                        .replace("top_", "")
                                        .replace("_reputation", ""));
                                return String.valueOf(database.getTopGamePlayerReputationByReputation(place - 1));
                            }
                        }
                    } catch (NumberFormatException ignored) {  }
                    return params;
            }
        }
        return params;
    }
}
