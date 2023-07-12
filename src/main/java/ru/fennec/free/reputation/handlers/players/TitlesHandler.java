package ru.fennec.free.reputation.handlers.players;

import ru.fennec.free.reputation.common.configs.ConfigManager;
import ru.fennec.free.reputation.common.interfaces.IGamePlayer;
import ru.fennec.free.reputation.handlers.database.configs.MainConfig;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class TitlesHandler {

    private MainConfig mainConfig;
    private Map<Long, String> sortedMap;

    public TitlesHandler(ConfigManager<MainConfig> mainConfigManager) {
        this.mainConfig = mainConfigManager.getConfigData();
        this.sortedMap = new LinkedHashMap<>(mainConfig.titles());
        sortMap();
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
        this.sortedMap = new LinkedHashMap<>(mainConfig.titles());
        sortMap();
    }

    private void sortMap() {
//        LinkedHashMap<Long, String> tempMap = new LinkedHashMap<>(this.sortedMap);
//        this.sortedMap.entrySet()
//                .stream()
//                .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
//                .forEachOrdered(x -> tempMap.put(x.getKey(), x.getValue()));
//        this.sortedMap.clear();
//        this.sortedMap.putAll(tempMap);

        this.sortedMap = this.sortedMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        this.sortedMap.forEach((key, value) -> System.out.println("Key: "+key+", value: "+value));
    }

}
