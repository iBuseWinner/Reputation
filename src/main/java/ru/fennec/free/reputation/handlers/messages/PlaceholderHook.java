package ru.fennec.free.reputation.handlers.messages;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.fennec.free.reputation.common.interfaces.IDatabase;
import ru.fennec.free.reputation.common.interfaces.IGamePlayer;
import ru.fennec.free.reputation.handlers.players.PlayersContainer;
import ru.fennec.free.reputation.handlers.players.TitlesHandler;

public class PlaceholderHook extends PlaceholderExpansion {

    private final String version;
    private final PlayersContainer playersContainer;
    private final IDatabase database;
    private final TitlesHandler titlesHandler;

    public PlaceholderHook(String version, PlayersContainer playersContainer, IDatabase database, TitlesHandler titlesHandler) {
        this.version = version;
        this.playersContainer = playersContainer;
        this.database = database;
        this.titlesHandler = titlesHandler;
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
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        IGamePlayer gamePlayer = playersContainer.getCachedPlayerByUUID(player.getUniqueId());
        if (gamePlayer != null) {
            switch (params.toLowerCase()) {
                case "reputation": //%reputation_reputation% - показывает игроку его репутацию
                    return String.valueOf(gamePlayer.getPlayerReputation());
                case "id": //%reputation_id% - показывает игроку его id из БД
                    return String.valueOf(gamePlayer.getId());
                case "favorites_amount": //%reputation_favorites_amount% - показывает, скольким игрокам игрок давал очко репутации
                    return String.valueOf(gamePlayer.getIDsWhomGaveReputation().size());
                case "title": //%reputation_title% - показывает звание игрока в зависимости от его репутации
                    return titlesHandler.getPlayerTitle(gamePlayer);
                default:
                    try {
                        if (params.toLowerCase().startsWith("top_online_")) {
                            if (params.toLowerCase().endsWith("_name")) {
                                int place = Integer.parseInt(params.toLowerCase()
                                        .replace("top_online_", "")
                                        .replace("_name", ""));
                                //Возвращает НИК игрока из ОНЛАЙН топа на N месте
                                return playersContainer.getTopGamePlayerByReputation(place - 1).getBukkitPlayer().getName();
                            } else if (params.toLowerCase().endsWith("_reputation")) {
                                int place = Integer.parseInt(params.toLowerCase()
                                        .replace("top_online_", "")
                                        .replace("_reputation", ""));
                                //Возвращает РЕПУТАЦИЮ игрока из ОНЛАЙН топа на N месте
                                return String.valueOf(playersContainer.getTopGamePlayerByReputation(place - 1).getPlayerReputation());
                            }
                        } else if (params.toLowerCase().startsWith("top_")) {
                            if (params.toLowerCase().endsWith("_name")) {
                                int place = Integer.parseInt(params.toLowerCase()
                                        .replace("top_", "")
                                        .replace("_name", ""));
                                //Возвращает НИК игрока из топа на N месте
                                return Bukkit.getOfflinePlayer(database.getTopGamePlayerUUIDByReputation(place)).getName();
                            } else if (params.toLowerCase().endsWith("_reputation")) {
                                int place = Integer.parseInt(params.toLowerCase()
                                        .replace("top_", "")
                                        .replace("_reputation", ""));
                                //Возвращает РЕПУТАЦИЮ игрока из ОНЛАЙН топа на N месте
                                return String.valueOf(database.getTopGamePlayerReputationByReputation(place));
                            }
                        }
                    } catch (NumberFormatException ignored) {  }
                    return params;
            }
        }
        return params;
    }
}
