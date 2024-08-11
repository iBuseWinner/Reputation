package ru.fennec.free.reputation.handlers.messages;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.fennec.free.reputation.common.configs.ConfigManager;
import ru.fennec.free.reputation.common.interfaces.IDatabase;
import ru.fennec.free.reputation.common.interfaces.IGamePlayer;
import ru.fennec.free.reputation.handlers.database.configs.MainConfig;
import ru.fennec.free.reputation.handlers.players.PlayersContainer;
import ru.fennec.free.reputation.handlers.players.TitlesHandler;

import java.util.LinkedHashMap;
import java.util.Map;

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
                        if (params.toLowerCase().startsWith("top_")) {
                            int place = Integer.parseInt(params.toLowerCase()
                                    .replace("top_", "")
                                    .replace("_name", "")
                                    .replace("_reputation", ""));
                            String name = getNthKey(place);
                            long rep = playersContainer.getCachedTopGamePlayers().get(name);
                            if (params.toLowerCase().endsWith("_name")) {
                                //Возвращает НИК игрока из топа на N месте
                                return name;
                            } else if (params.toLowerCase().endsWith("_reputation")) {
                                //Возвращает РЕПУТАЦИЮ игрока из топа на N месте
                                return String.valueOf(rep);
                            }
                        }
                    } catch (NumberFormatException ignored) {
                    }
                    return params;
            }
        }
        return params;
    }

    private String getNthKey(int place) {
        int currentPlace = 0;

        for (Map.Entry<String, Long> entry : playersContainer.getCachedTopGamePlayers().entrySet()) {
            if (currentPlace == place) {
                return entry.getKey();
            }
            currentPlace++;
        }

        return "";
    }

    public void updateConfigData(ConfigManager<MainConfig> mainConfigManager) {
        this.mainConfig = mainConfigManager.getConfigData();
    }
}
