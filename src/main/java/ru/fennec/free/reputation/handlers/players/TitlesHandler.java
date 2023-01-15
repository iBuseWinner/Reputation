package ru.fennec.free.reputation.handlers.players;

import ru.fennec.free.reputation.common.configs.ConfigManager;
import ru.fennec.free.reputation.common.interfaces.IGamePlayer;
import ru.fennec.free.reputation.handlers.database.configs.MainConfig;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

public class TitlesHandler {

    private MainConfig mainConfig;
    private Map<Long, String> sortedMap;

    public TitlesHandler(ConfigManager<MainConfig> mainConfigManager) {
        this.mainConfig = mainConfigManager.getConfigData();
        this.sortedMap = new TreeMap<>(mainConfig.titles());
    }

    public String getPlayerTitle(IGamePlayer gamePlayer) {
        AtomicReference<String> playerTitle = new AtomicReference<>("");

        for (long reputation : sortedMap.keySet()) {
            if (gamePlayer.getPlayerReputation() >= reputation) {
                playerTitle.set(sortedMap.get(reputation));
                break;
            }
        }

        return playerTitle.get();
    }

    public void updateConfigData(ConfigManager<MainConfig> mainConfigManager) {
        this.mainConfig = mainConfigManager.getConfigData();
        this.sortedMap = new TreeMap<>(mainConfig.titles());
    }

}
