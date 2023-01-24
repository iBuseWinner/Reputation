package ru.fennec.free.reputation.handlers.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import ru.fennec.free.reputation.common.configs.ConfigManager;
import ru.fennec.free.reputation.common.interfaces.IDatabase;
import ru.fennec.free.reputation.common.interfaces.IGamePlayer;
import ru.fennec.free.reputation.handlers.database.configs.MainConfig;
import ru.fennec.free.reputation.handlers.database.configs.MessagesConfig;
import ru.fennec.free.reputation.handlers.messages.MessageManager;
import ru.fennec.free.reputation.handlers.players.GamePlayer;
import ru.fennec.free.reputation.handlers.players.PlayersContainer;

public class PlayerConnectionListener implements Listener {

    private final Plugin plugin;
    private final IDatabase database;
    private final PlayersContainer playersContainer;
    private final MessageManager messageManager;
    private MainConfig mainConfig;
    private MessagesConfig messagesConfig;

    public PlayerConnectionListener(Plugin plugin, ConfigManager<MainConfig> mainConfigManager,
                                    ConfigManager<MessagesConfig> messagesConfigManager,
                                    IDatabase database, PlayersContainer playersContainer,
                                    MessageManager messageManager) {
        this.plugin = plugin;
        this.mainConfig = mainConfigManager.getConfigData();
        this.messagesConfig = messagesConfigManager.getConfigData();
        this.database = database;
        this.playersContainer = playersContainer;
        this.messageManager = messageManager;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (playersContainer.getCachedPlayerByUUID(player.getUniqueId()) == null) { //Игрок не в кэше.
                IGamePlayer gamePlayer = database.wrapPlayer(player); //Получение игрока из бд
                if (gamePlayer == null) { //Игрока нет в базе данных, поэтому добавляем его туда
                    gamePlayer = new GamePlayer(player); //Создание пустого игрока с id = -1
                    database.insertNewPlayer(gamePlayer); //Запись пустого игрока
                    gamePlayer = database.wrapPlayer(player); //Получение игрока с id из бд

                    player.sendMessage(messageManager.parsePlaceholders(gamePlayer,
                            messagesConfig.playerSection().notifyOnFirstJoin()));
                } else {
                    player.sendMessage(messageManager.parsePlaceholders(gamePlayer,
                            messagesConfig.playerSection().notifyOnJoin()));
                }
                playersContainer.addCachedPlayer(gamePlayer); //Сохранение игрока в кэше до его выхода с сервера
            } //Игрок уже в кэше??? Как.! Ну мб ничего делать не над...
        }, mainConfig.bungeeMode() ? 2 : 0); //Если несколько серверов, то делаем задержку 2 тика
        // (возможно, придётся увеличивать), если 1 сервер, то задержка не нужна

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

    public void updateConfigData(ConfigManager<MainConfig> mainConfigManager,
                                 ConfigManager<MessagesConfig> messagesConfigManager) {
        this.mainConfig = mainConfigManager.getConfigData();
        this.messagesConfig = messagesConfigManager.getConfigData();
    }

}
