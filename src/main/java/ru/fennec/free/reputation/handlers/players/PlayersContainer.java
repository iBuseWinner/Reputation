package ru.fennec.free.reputation.handlers.players;

import ru.fennec.free.reputation.common.interfaces.IGamePlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class PlayersContainer {

    private final List<IGamePlayer> cachedPlayers;

    public PlayersContainer() {
        this.cachedPlayers = new ArrayList<>();
    }

    public IGamePlayer getCachedPlayerByUUID(UUID uuid) {
        IGamePlayer gamePlayer = null;
        for (IGamePlayer target : cachedPlayers) {
            if (uuid.equals(target.getGamePlayerUUID())) {
                gamePlayer = target;
            }
        }
        return gamePlayer;
    }

    public void addCachedPlayer(IGamePlayer gamePlayer) {
        this.cachedPlayers.add(gamePlayer);
    }

    public void removeCachedPlayerByUUID(UUID uuid) {
        this.cachedPlayers.removeIf(gamePlayer -> uuid.equals(gamePlayer.getGamePlayerUUID()));
    }

    public List<IGamePlayer> getAllCachedPlayers() {
        return this.cachedPlayers;
    }

    //А вот фиг тут плавал. Это онлайн игроки, кек.
    public IGamePlayer getTopGamePlayerByReputation(int place) {
        return cachedPlayers
                .stream().max(Comparator.comparingLong(IGamePlayer::getPlayerReputation)).get();
    }
}
