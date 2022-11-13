package ru.fennec.free.reputation.handlers.messages;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import ru.fennec.free.reputation.common.interfaces.IGamePlayer;
import ru.fennec.free.reputation.handlers.players.PlayersContainer;

public class PlaceholderHook extends PlaceholderExpansion {

    private final String version;
    private final PlayersContainer playersContainer;

    public PlaceholderHook(String version, PlayersContainer playersContainer) {
        this.version = version;
        this.playersContainer = playersContainer;
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
                    return params;
            }
        }
        return params;
    }
}
