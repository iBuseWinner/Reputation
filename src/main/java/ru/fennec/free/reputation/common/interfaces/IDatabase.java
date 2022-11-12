package ru.fennec.free.reputation.common.interfaces;

import org.bukkit.entity.Player;

public interface IDatabase {

    void initializeTables();

    void insertNewPlayer(IGamePlayer gamePlayer);

    void savePlayer(IGamePlayer gamePlayer);

    void saveAction(IGamePlayer acting, IGamePlayer target);

    IGamePlayer wrapPlayer(Player player);

}
