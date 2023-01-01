package ru.fennec.free.reputation.common.interfaces;

import org.bukkit.entity.Player;

public interface IDatabase {

    void initializeTables();

    void insertNewPlayer(IGamePlayer gamePlayer);

    void savePlayer(IGamePlayer gamePlayer);

    void saveAction(IGamePlayer acting, IGamePlayer target);

    void deleteAction(IGamePlayer gamePlayer);

    IGamePlayer wrapPlayer(Player player);

}
