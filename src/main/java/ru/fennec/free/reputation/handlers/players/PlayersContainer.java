package ru.fennec.free.reputation.handlers.players;

import ru.fennec.free.reputation.common.interfaces.IGamePlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class PlayersContainer {

    //Список кэш игроков
    private final List<IGamePlayer> cachedPlayers;

    public PlayersContainer() {
        this.cachedPlayers = new ArrayList<>();
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
     * Получить топ-N игрока по репутации
     *
     * @param place N - место игрока в топе (начинается с нуля)
     * @return IGamePlayer - игрока на N месте. Ошибка стоп ноль ноль ноль ноль если нет такого
     */
    public IGamePlayer getTopGamePlayerByReputation(int place) {
        return cachedPlayers //Тут ошибка в методе ору, возвращается всегда топ с первого места :/
                .stream().max(Comparator.comparingLong(IGamePlayer::getPlayerReputation)).get();
    }
}
