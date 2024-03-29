package ru.fennec.free.reputation.common.interfaces;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface IDatabase {

    void initializeTables();

    void insertNewPlayer(IGamePlayer gamePlayer);

    void savePlayer(IGamePlayer gamePlayer);

    void saveAction(IGamePlayer acting, IGamePlayer target, String action);

    void deleteAction(IGamePlayer gamePlayer);

    void saveCommand(IGamePlayer acting, String commandId);

    void deleteCommand(IGamePlayer gamePlayer);

    boolean isUsedCommand(IGamePlayer gamePlayer, String commandId);

    IGamePlayer wrapPlayer(Player player);

    UUID getTopGamePlayerUUIDByReputation(int place);

    Long getTopGamePlayerReputationByReputation(int place);

}
