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
    private List<Long> IDsWhomGaveReputation;

    public GamePlayer(Player bukkitPlayer) {
        this.id = -1;
        this.bukkitPlayer = bukkitPlayer;
        this.gamePlayerUUID = bukkitPlayer.getUniqueId();
        this.playerReputation = 0;
        this.IDsWhomGaveReputation = new ArrayList<>();
    }

    public GamePlayer(Player bukkitPlayer, long playerReputation) {
        this.id = -1;
        this.bukkitPlayer = bukkitPlayer;
        this.gamePlayerUUID = bukkitPlayer.getUniqueId();
        this.playerReputation = playerReputation;
        this.IDsWhomGaveReputation = new ArrayList<>();
    }

    public GamePlayer(Player bukkitPlayer, long playerReputation, List<Long> IDsWhomGaveReputation) {
        this.id = -1;
        this.bukkitPlayer = bukkitPlayer;
        this.gamePlayerUUID = bukkitPlayer.getUniqueId();
        this.playerReputation = playerReputation;
        this.IDsWhomGaveReputation = IDsWhomGaveReputation;
    }

    public GamePlayer(long id, Player bukkitPlayer, long playerReputation, List<Long> IDsWhomGaveReputation) {
        this.id = id;
        this.bukkitPlayer = bukkitPlayer;
        this.gamePlayerUUID = bukkitPlayer.getUniqueId();
        this.playerReputation = playerReputation;
        this.IDsWhomGaveReputation = IDsWhomGaveReputation;
    }

    public GamePlayer(long id, UUID playerUUID, long playerReputation) {
        this.id = id;
        this.gamePlayerUUID = playerUUID;
        this.bukkitPlayer = getBukkitPlayer();
        this.playerReputation = playerReputation;
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
    public List<Long> getIDsWhomGaveReputation() {
        return IDsWhomGaveReputation;
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
    public void setIDsWhomGaveReputation(List<Long> IDsWhomGaveReputation) {
        this.IDsWhomGaveReputation = IDsWhomGaveReputation;
    }
}