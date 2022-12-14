package ru.fennec.free.reputation.handlers.messages;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import ru.fennec.free.reputation.common.configs.ConfigManager;
import ru.fennec.free.reputation.common.interfaces.IGamePlayer;
import ru.fennec.free.reputation.common.replacers.StaticReplacer;
import ru.fennec.free.reputation.handlers.database.configs.MessagesConfig;

public class MessageManager {

    private MessagesConfig messagesConfig;

    public MessageManager(ConfigManager<MessagesConfig> messagesConfigManager) {
        this.messagesConfig = messagesConfigManager.getConfigData();
    }

    public String parseColors(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String parsePluginPlaceholders(String message) {
        return StaticReplacer.replacer()
                .set("prefix", messagesConfig.prefix())
                .apply(parseColors(message));
    }

    public String parsePlaceholders(IGamePlayer gamePlayer, String message) {
        return PlaceholderAPI
                .setPlaceholders(gamePlayer.getBukkitPlayer(),
                        StaticReplacer.replacer()
                                .set("player_reputation", gamePlayer.getPlayerReputation())
                                .set("player_id", gamePlayer.getId())
                                .apply(parsePluginPlaceholders(message)));
    }

}
