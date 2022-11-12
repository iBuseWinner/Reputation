package ru.fennec.free.reputation.common.interfaces;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface IGamePlayer {

    long getId();

    Player getBukkitPlayer();

    UUID getGamePlayerUUID();

    long getPlayerReputation();

    List<Long> getUUIDsWhomGaveReputation();

    void setId(long id);

    void setGamePlayerUUID(UUID gamePlayerUUID);

    void setPlayerReputation(long playerReputation);

    void setUUIDsWhomGaveReputation(List<Long> UUIDsWhomGaveReputation);

}
