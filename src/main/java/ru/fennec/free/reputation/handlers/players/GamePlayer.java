package ru.fennec.free.reputation.handlers.players;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.fennec.free.reputation.common.interfaces.IGamePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GamePlayer implements IGamePlayer {
    private long id;
    private Player bukkitPlayer;
    private UUID gamePlayerUUID;
    private long playerReputation;
    private boolean acceptReputation;
    private List<Long> IDsWhomGaveReputation;
    private List<Long> IDsWhomTookReputation;

    public GamePlayer(Player bukkitPlayer) {
        this.id = -1;
        this.bukkitPlayer = bukkitPlayer;
        this.gamePlayerUUID = bukkitPlayer.getUniqueId();
        this.playerReputation = 0;
        this.acceptReputation = false;
        this.IDsWhomGaveReputation = new ArrayList<>();
    }

    public GamePlayer(long id, UUID playerUUID, long playerReputation, boolean acceptReputation) {
        this.id = id;
        this.gamePlayerUUID = playerUUID;
        this.bukkitPlayer = getBukkitPlayer();
        this.playerReputation = playerReputation;
        this.acceptReputation = acceptReputation;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public Player getBukkitPlayer() {
        return (bukkitPlayer == null ? Bukkit.getPlayer(gamePlayerUUID) : bukkitPlayer);
    }

    @Override
    public UUID getGamePlayerUUID() {
        return gamePlayerUUID;
    }

    @Override
    public long getPlayerReputation() {
        return playerReputation;
    }

    @Override
    public boolean acceptReputation() {
        return acceptReputation;
    }

    @Override
    public List<Long> getIDsWhomGaveReputation() {
        return IDsWhomGaveReputation;
    }

    public List<Long> getIDsWhomTookReputation() {
        return IDsWhomTookReputation;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public void setGamePlayerUUID(UUID gamePlayerUUID) {
        this.gamePlayerUUID = gamePlayerUUID;
    }

    @Override
    public void setPlayerReputation(long playerReputation) {
        this.playerReputation = playerReputation;
    }

    @Override
    public void setAcceptReputation(boolean acceptReputation) {
        this.acceptReputation = acceptReputation;
    }

    @Override
    public void setIDsWhomGaveReputation(List<Long> IDsWhomGaveReputation) {
        this.IDsWhomGaveReputation = IDsWhomGaveReputation;
    }

    public void setIDsWhomTookReputation(List<Long> IDsWhomTookReputation) {
        this.IDsWhomTookReputation = IDsWhomTookReputation;
    }
}
