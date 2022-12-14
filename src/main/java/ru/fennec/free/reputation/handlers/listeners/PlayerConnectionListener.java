package ru.fennec.free.reputation.handlers.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.fennec.free.reputation.common.configs.ConfigManager;
import ru.fennec.free.reputation.common.interfaces.IDatabase;
import ru.fennec.free.reputation.common.interfaces.IGamePlayer;
import ru.fennec.free.reputation.handlers.database.configs.MainConfig;
import ru.fennec.free.reputation.handlers.database.configs.MessagesConfig;
import ru.fennec.free.reputation.handlers.messages.MessageManager;
import ru.fennec.free.reputation.handlers.players.GamePlayer;
import ru.fennec.free.reputation.handlers.players.PlayersContainer;

public class PlayerConnectionListener implements Listener {

    private final MainConfig mainConfig;
    private final MessagesConfig messagesConfig;
    private final IDatabase database;
    private final PlayersContainer playersContainer;
    private final MessageManager messageManager;

    public PlayerConnectionListener(ConfigManager<MainConfig> mainConfigManager, ConfigManager<MessagesConfig> messagesConfigManager,
                                    IDatabase database, PlayersContainer playersContainer, MessageManager messageManager) {
        this.mainConfig = mainConfigManager.getConfigData();
        this.messagesConfig = messagesConfigManager.getConfigData();
        this.database = database;
        this.playersContainer = playersContainer;
        this.messageManager = messageManager;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (playersContainer.getCachedPlayerByUUID(player.getUniqueId()) == null) { //Игрок не в кэше.
            IGamePlayer gamePlayer = database.wrapPlayer(player); //Получение игрока из бд
            if (gamePlayer == null) { //Игрока нет в базе данных, поэтому добавляем его туда
                gamePlayer = new GamePlayer(player); //Создание пустого игрока с id = -1
                database.insertNewPlayer(gamePlayer); //Запись пустого игрока
                gamePlayer = database.wrapPlayer(player); //Получение игрока с id из бд

                player.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().notifyOnFirstJoin()));
            } else {
                player.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().notifyOnJoin()));
            }
            playersContainer.addCachedPlayer(gamePlayer); //Сохранение игрока в кэше до его выхода с сервера
        } //Игрок уже в кэше??? Как.! Ну мб ничего делать не над...

    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        IGamePlayer gamePlayer = playersContainer.getCachedPlayerByUUID(player.getUniqueId());
        if (gamePlayer != null) { //Игрок в кэше
            database.savePlayer(gamePlayer);
            playersContainer.removeCachedPlayerByUUID(player.getUniqueId());
        } //Игрока нет в кэше, ну блин как...
    }

}
