package ru.fennec.free.reputation.handlers.messages;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.md_5.bungee.api.ChatColor;
import ru.fennec.free.reputation.common.configs.ConfigManager;
import ru.fennec.free.reputation.common.interfaces.IGamePlayer;
import ru.fennec.free.reputation.common.replacers.StaticReplacer;
import ru.fennec.free.reputation.handlers.database.configs.MessagesConfig;
import ru.fennec.free.reputation.handlers.players.TitlesHandler;

public class MessageManager {

    private MessagesConfig messagesConfig;
    private final TitlesHandler titlesHandler;
    private MiniMessage serializer;

    public MessageManager(ConfigManager<MessagesConfig> messagesConfigManager, TitlesHandler titlesHandler) {
        this.messagesConfig = messagesConfigManager.getConfigData();
        this.titlesHandler = titlesHandler;
        this.serializer = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.color())
                        .resolver(StandardTags.decorations())
                        .resolver(StandardTags.font())
                        .resolver(StandardTags.gradient())
                        .resolver(StandardTags.newline())
                        .resolver(StandardTags.rainbow())
                        .build())
                .build();
    }

    private Component parseColors(String message) {
        // <-- START --> Convert legacy colors to MiniMessage
        message = message
                .replaceAll("&0", "<black>")
                .replaceAll("&1", "<dark_blue>")
                .replaceAll("&2", "<dark_green>")
                .replaceAll("&3", "<dark_aqua>")
                .replaceAll("&4", "<dark_red>")
                .replaceAll("&5", "<dark_purple>")
                .replaceAll("&6", "<gold>")
                .replaceAll("&7", "<gray>")
                .replaceAll("&8", "<dark_gray>")
                .replaceAll("&9", "<blue>")
                .replaceAll("&a", "<green>")
                .replaceAll("&b", "<aqua>")
                .replaceAll("&c", "<red>")
                .replaceAll("&d", "<light_purple>")
                .replaceAll("&e", "<yellow>")
                .replaceAll("&f", "<white>")
                .replaceAll("&k", "<obf>")
                .replaceAll("&l", "<b>")
                .replaceAll("&m", "<st>")
                .replaceAll("&n", "<u>")
                .replaceAll("&o", "<i>")
                .replaceAll("&r", "<reset>");
        // <-- END --> Convert legacy colors to MiniMessage

        return serializer.deserialize(message);
    }

    /***
     * Заменяет все (1) общие плейсхолдеры
     *
     * @param gamePlayer игрок, на которого должны быть нацелены плейсхолдеры
     * @param message исходное сообщение
     * @return отформатированное сообщение
     */
    public Component parsePluginPlaceholders(String message) {
        return parseColors(
                StaticReplacer.replacer()
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
    public Component parsePlaceholders(IGamePlayer gamePlayer, String message) {
        return parseColors(PlaceholderAPI
                .setPlaceholders(gamePlayer.getBukkitPlayer(),
                        StaticReplacer.replacer()
                                .set("player_reputation", gamePlayer.getPlayerReputation())
                                .set("player_id", gamePlayer.getId())
                                .set("player_name", gamePlayer.getBukkitPlayer().getName())
                                .set("player_favorites_amount", gamePlayer.getIDsWhomGaveReputation().size())
                                .set("player_title", titlesHandler.getPlayerTitle(gamePlayer))
                                .set("prefix", messagesConfig.prefix())
                                .apply(message)));
    }

    public void updateConfigData(ConfigManager<MessagesConfig> messagesConfigManager) {
        this.messagesConfig = messagesConfigManager.getConfigData();
    }

}
