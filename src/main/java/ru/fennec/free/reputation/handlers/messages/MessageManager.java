package ru.fennec.free.reputation.handlers.messages;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import ru.fennec.free.reputation.common.configs.ConfigManager;
import ru.fennec.free.reputation.common.interfaces.IGamePlayer;
import ru.fennec.free.reputation.common.replacers.StaticReplacer;
import ru.fennec.free.reputation.handlers.database.configs.MessagesConfig;
import ru.fennec.free.reputation.handlers.players.TitlesHandler;

public class MessageManager {

    private MessagesConfig messagesConfig;
    private TitlesHandler titlesHandler;

    public MessageManager(ConfigManager<MessagesConfig> messagesConfigManager, TitlesHandler titlesHandler) {
        this.messagesConfig = messagesConfigManager.getConfigData();
        this.titlesHandler = titlesHandler;
    }

    private String parseColors(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /***
     * Заменяет все (1) общие плейхолдеры из плагина, а дальше конвертирует цвета
     *
     * @param message сообщение, в котором необходимо заменить
     * @return сообщение, в котором плейсхолдеры заменены, а цвета сконвертированы
     */
    public String parsePluginPlaceholders(String message) {
        return parseColors(StaticReplacer.replacer()
                .set("prefix", messagesConfig.prefix())
                .apply(message));
    }

    /***
     * Заменяет все плейсхолдеры из плагина, сначала общие, потом личные (reputation, id), дальше из PlaceholderAPI
     *
     * @param gamePlayer игрок, на которого должны быть нацелены плейсхолдеры
     * @param message исходное сообщение
     * @return отформатированное сообщение
     */
    public String parsePlaceholders(IGamePlayer gamePlayer, String message) {
        return PlaceholderAPI
                .setPlaceholders(gamePlayer.getBukkitPlayer(),
                        StaticReplacer.replacer()
                                .set("player_reputation", gamePlayer.getPlayerReputation())
                                .set("player_id", gamePlayer.getId())
                                .set("player_name", gamePlayer.getBukkitPlayer().getName())
                                .set("player_favorites_amount", gamePlayer.getIDsWhomGaveReputation().size())
                                .set("player_title", titlesHandler.getPlayerTitle(gamePlayer))
                                .apply(parsePluginPlaceholders(message)));
    }

    public void updateConfigData(ConfigManager<MessagesConfig> messagesConfigManager) {
        this.messagesConfig = messagesConfigManager.getConfigData();
    }

}
