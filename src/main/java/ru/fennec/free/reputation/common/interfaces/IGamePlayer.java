package ru.fennec.free.reputation.common.interfaces;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface IGamePlayer {

    long getId(); //id из БД

    Player getBukkitPlayer(); //Bukkit игрок

    UUID getGamePlayerUUID(); //UUID от Bukkit игрока

    long getPlayerReputation(); //Репутация игрока

    List<Long> getIDsWhomGaveReputation(); //Список игроков, которым игрок давал очки репутации

    List<Long> getIDsWhomTookReputation(); //Список игроков, у которых игрок отнимал очки репутации

    void setId(long id);

    void setGamePlayerUUID(UUID gamePlayerUUID);

    void setPlayerReputation(long playerReputation);

    void setIDsWhomGaveReputation(List<Long> IDsWhomGaveReputation);

    void setIDsWhomTookReputation(List<Long> IDsWhomTookReputation);

}
