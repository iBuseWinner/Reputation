package ru.fennec.free.reputation.handlers.players;

import org.bukkit.Bukkit;
import ru.fennec.free.reputation.ReputationPlugin;
import ru.fennec.free.reputation.common.configs.ConfigManager;
import ru.fennec.free.reputation.common.interfaces.IDatabase;
import ru.fennec.free.reputation.common.interfaces.IGamePlayer;
import ru.fennec.free.reputation.handlers.database.configs.MainConfig;

import java.util.*;
import java.util.stream.Collectors;

public class PlayersContainer {

    private ReputationPlugin plugin;
    private IDatabase database;
    private MainConfig mainConfig;

    //Список кэш игроков в топе по репутации
    private Map<String, Long> cachedTopGamePlayers;
    //Список кэш игроков
    private final List<IGamePlayer> cachedPlayers;

    public PlayersContainer(ReputationPlugin plugin, IDatabase database, ConfigManager<MainConfig> mainConfigManager) {
        this.plugin = plugin;
        this.database = database;
        this.mainConfig = mainConfigManager.getConfigData();
        this.cachedPlayers = new ArrayList<>();
        this.cachedTopGamePlayers = new LinkedHashMap<>();
        updateCachedTopPlayers();
    }

    /***
     * Получение IGamePlayer из списка кэш игроков по UUID игрока (Player#getUniqueId())
     *
     * @param uuid UUID игрока
     * @return IGamePlayer или null, если игрока нет в кэше плагина
     */
    public IGamePlayer getCachedPlayerByUUID(UUID uuid) {
        IGamePlayer gamePlayer = null;
        for (IGamePlayer target : cachedPlayers) {
            if (uuid.equals(target.getGamePlayerUUID())) {
                gamePlayer = target;
            }
        }
        return gamePlayer;
    }

    /***
     * Добавить игрока IGamePlayer в кэш плагина
     *
     * @param gamePlayer объект IGamePlayer, который записывается в кэш
     */
    public void addCachedPlayer(IGamePlayer gamePlayer) {
        this.cachedPlayers.add(gamePlayer);
    }

    /***
     * Удалить игрока из кэша плагина по его UUID (Player#getUniqueId() или IGamePlayer#getGamePlayerUUID())
     *
     * @param uuid UUID, по которому идёт поиск игрока в кэше
     */
    public void removeCachedPlayerByUUID(UUID uuid) {
        this.cachedPlayers.removeIf(gamePlayer -> uuid.equals(gamePlayer.getGamePlayerUUID()));
    }

    /***
     * Возвращает полный список кэшированных игроков
     *
     * @return Список из IGamePlayer. Пустой список - нет игроков в кэше
     */
    public List<IGamePlayer> getAllCachedPlayers() {
        return this.cachedPlayers;
    }

    /***
     * Получить топ-N онлайн игрока по репутации
     *
     * @param place N - место игрока в топе (начинается с нуля)
     * @return IGamePlayer - игрока на N месте. Ошибка стоп ноль ноль ноль ноль если нет такого
     */
    public IGamePlayer getTopGamePlayerByReputation(int place) {
//        return cachedPlayers //Тут ошибка в методе ору, возвращается всегда топ с первого места :/
//                .stream().max(Comparator.comparingLong(IGamePlayer::getPlayerReputation)).get();
        if (place >= cachedPlayers.size()) {
            return null;
        }
        return cachedPlayers
                .stream()
//                .sorted((f1, f2) -> Long.compare(f2.getPlayerReputation(), f1.getPlayerReputation()))
                .sorted(Comparator.comparingLong(IGamePlayer::getPlayerReputation).reversed())
                .collect(Collectors.toList())
                .get(place);
    }

    /**
     * Список обновляется каждые N тиков (настраивается в конфиге, параметр topUpdateFrequency)
     * @return Map<String,Long> - список топ игроков по репутации (String - ник, Long - очки репутации)
     */
    public Map<String, Long> getCachedTopGamePlayers() {
        return cachedTopGamePlayers;
    }

    private void updateCachedTopPlayers() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            cachedTopGamePlayers.clear();
            Map<String, Long> mergedTopPlayers = new HashMap<>();

            for (int i = 0; i < mainConfig.topAmount(); i++) {
                IGamePlayer onlinePlayer = getTopGamePlayerByReputation(i);
                if (onlinePlayer != null) {
                    mergedTopPlayers.put(onlinePlayer.getBukkitPlayer().getName(), onlinePlayer.getPlayerReputation());
                }
            }

            for (int i = 0; i < mainConfig.topAmount(); i++) {
                UUID topUUIDByReputation = database.getTopGamePlayerUUIDByReputation(i+1);
                if (topUUIDByReputation != null) {
                    String playerName = Bukkit.getOfflinePlayer(topUUIDByReputation).getName();
                    long playerReputation = database.getTopGamePlayerReputationByReputation(i+1);
                    if (!mergedTopPlayers.containsKey(playerName)) {
                        mergedTopPlayers.put(playerName, playerReputation);
                    }
                }
            }

            cachedTopGamePlayers = mergedTopPlayers.entrySet()
                    .stream()
                    .sorted((entry1, entry2) -> Long.compare(entry2.getValue(), entry1.getValue()))
                    .limit(mainConfig.topAmount())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new
                    ));
        }, 0, mainConfig.topUpdateFrequency());
    }

    public void updateConfigData(ConfigManager<MainConfig> mainConfigManager) {
        this.mainConfig = mainConfigManager.getConfigData();
    }

}
