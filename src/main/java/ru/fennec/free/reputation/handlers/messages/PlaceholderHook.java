package ru.fennec.free.reputation.handlers.messages;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.fennec.free.reputation.common.configs.ConfigManager;
import ru.fennec.free.reputation.common.interfaces.IDatabase;
import ru.fennec.free.reputation.common.interfaces.IGamePlayer;
import ru.fennec.free.reputation.handlers.database.configs.MainConfig;
import ru.fennec.free.reputation.handlers.enums.OrderBy;
import ru.fennec.free.reputation.handlers.players.PlayersContainer;
import ru.fennec.free.reputation.handlers.players.TitlesHandler;

import java.util.LinkedHashMap;

public class PlaceholderHook extends PlaceholderExpansion {

    private final String version;
    private final PlayersContainer playersContainer;
    private final IDatabase database;
    private final TitlesHandler titlesHandler;
    private MainConfig mainConfig;

    public PlaceholderHook(String version, PlayersContainer playersContainer, IDatabase database,
                           TitlesHandler titlesHandler, ConfigManager<MainConfig> mainConfigManager) {
        this.version = version;
        this.playersContainer = playersContainer;
        this.database = database;
        this.titlesHandler = titlesHandler;
        this.mainConfig = mainConfigManager.getConfigData();
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
                    String color = "";
                    long reputation = gamePlayer.getPlayerReputation();
                    if (mainConfig.color().enable()) {
                        if (reputation < 0) {
                            color = mainConfig.color().negativeReputation();
                        } else if (reputation == 0) {
                            color = mainConfig.color().neutralReputation();
                        } else {
                            color = mainConfig.color().positiveReputation();
                        }
                    }
                    return color + reputation;
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
                                return playersContainer.getTopGamePlayerByReputation(place - 1, OrderBy.DESC).getBukkitPlayer().getName();
                            } else if (params.toLowerCase().endsWith("_reputation")) {
                                int place = Integer.parseInt(params.toLowerCase()
                                        .replace("top_online_", "")
                                        .replace("_reputation", ""));
                                //Возвращает РЕПУТАЦИЮ игрока из ОНЛАЙН топа на N месте
                                return String.valueOf(playersContainer.getTopGamePlayerByReputation(place - 1, OrderBy.DESC).getPlayerReputation());
                            }
                        } else if (params.toLowerCase().startsWith("top_")) {
                            if (params.toLowerCase().endsWith("_name")) {
                                int place = Integer.parseInt(params.toLowerCase()
                                        .replace("top_", "")
                                        .replace("_name", ""));
                                //Возвращает НИК игрока из топа на N месте (Репутация по убыванию)
                                return Bukkit.getOfflinePlayer(database.getTopGamePlayerUUIDByReputation(place, OrderBy.DESC)).getName();
                            } else if (params.toLowerCase().endsWith("_reputation")) {
                                int place = Integer.parseInt(params.toLowerCase()
                                        .replace("top_", "")
                                        .replace("_reputation", ""));
                                //Возвращает РЕПУТАЦИЮ игрока из ОНЛАЙН топа на N месте (Репутация по убыванию)
                                return String.valueOf(database.getTopGamePlayerReputationByReputation(place, OrderBy.DESC));
                            }
                        } else if (params.toLowerCase().startsWith("badtop_online_")) {
                            if (params.toLowerCase().endsWith("_name")) {
                                int place = Integer.parseInt(params.toLowerCase()
                                        .replace("badtop_online_", "")
                                        .replace("_name", ""));
                                //Возвращает НИК игрока из ОНЛАЙН топа ПЛОХИХ ИГРОКОВ на N месте (Репутация по возрастанию)
                                return playersContainer.getTopGamePlayerByReputation(place - 1, OrderBy.ASC).getBukkitPlayer().getName();
                            } else if (params.toLowerCase().endsWith("_reputation")) {
                                int place = Integer.parseInt(params.toLowerCase()
                                        .replace("badtop_online_", "")
                                        .replace("_reputation", ""));
                                //Возвращает РЕПУТАЦИЮ игрока из ОНЛАЙН топа ПЛОХИХ ИГРОКОВ на N месте (Репутация по возрастанию)
                                return String.valueOf(playersContainer.getTopGamePlayerByReputation(place - 1, OrderBy.ASC).getPlayerReputation());
                            }
                        } else if (params.toLowerCase().startsWith("badtop_")) {
                            if (params.toLowerCase().endsWith("_name")) {
                                int place = Integer.parseInt(params.toLowerCase()
                                        .replace("badtop_", "")
                                        .replace("_name", ""));
                                //Возвращает НИК игрока из топа ПЛОХИХ ИГРОКОВ на N месте (Репутация по возрастанию)
                                return Bukkit.getOfflinePlayer(database.getTopGamePlayerUUIDByReputation(place, OrderBy.ASC)).getName();
                            } else if (params.toLowerCase().endsWith("_reputation")) {
                                int place = Integer.parseInt(params.toLowerCase()
                                        .replace("badtop_", "")
                                        .replace("_reputation", ""));
                                //Возвращает РЕПУТАЦИЮ игрока из ОНЛАЙН топа ПЛОХИХ ИГРОКОВ на N месте (Репутация по возрастанию)
                                return String.valueOf(database.getTopGamePlayerReputationByReputation(place, OrderBy.ASC));
                            }
                        }
                    } catch (NumberFormatException ignored) {
                    }
                    return params;
            }
        }
        return params;
    }

    public void updateConfigData(ConfigManager<MainConfig> mainConfigManager) {
        this.mainConfig = mainConfigManager.getConfigData();
    }
}
