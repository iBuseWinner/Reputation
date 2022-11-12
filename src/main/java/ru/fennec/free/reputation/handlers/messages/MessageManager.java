package ru.fennec.free.reputation.handlers.messages;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import ru.fennec.free.reputation.common.configs.ConfigManager;
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
        return parseColors(message);
    }

    public String parsePlaceholders(Player player, String message) {
        return PlaceholderAPI.setPlaceholders(player, message);
    }

}
