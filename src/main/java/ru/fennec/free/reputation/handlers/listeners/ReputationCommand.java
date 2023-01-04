package ru.fennec.free.reputation.handlers.listeners;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.fennec.free.reputation.common.abstracts.AbstractCommand;
import ru.fennec.free.reputation.common.configs.ConfigManager;
import ru.fennec.free.reputation.common.interfaces.IDatabase;
import ru.fennec.free.reputation.common.interfaces.IGamePlayer;
import ru.fennec.free.reputation.handlers.database.configs.MainConfig;
import ru.fennec.free.reputation.handlers.database.configs.MessagesConfig;
import ru.fennec.free.reputation.handlers.messages.MessageManager;
import ru.fennec.free.reputation.handlers.players.PlayersContainer;

import java.util.ArrayList;

public class ReputationCommand extends AbstractCommand {

    private final ConfigManager<MessagesConfig> messagesConfigManager;
    private final ConfigManager<MainConfig> mainConfigManager;
    private MessagesConfig messagesConfig;
    private final IDatabase database;
    private final PlayersContainer playersContainer;
    private final MessageManager messageManager;

    public ReputationCommand(Plugin plugin, ConfigManager<MessagesConfig> messagesConfigManager, ConfigManager<MainConfig> mainConfigManager, IDatabase database,
                             PlayersContainer playersContainer, MessageManager messageManager) {
        super(plugin, "reputation");
        this.messagesConfigManager = messagesConfigManager;
        this.mainConfigManager = mainConfigManager;
        this.messagesConfig = messagesConfigManager.getConfigData();
        this.database = database;
        this.playersContainer = playersContainer;
        this.messageManager = messageManager;
    }

    @Override
    public void execute(CommandSender commandSender, String label, String[] args) {
        switch (args.length) {
            case 1:
                switch (args[0].toLowerCase()) {
                    case "help" -> sendHelp(commandSender); // /rep help
                    case "info", "me", "self" -> sendSelfInfo(commandSender); // /rep me
                    case "reload" -> reloadPlugin(commandSender); // /rep reload
                    default -> sendPlayerInfo(commandSender, args[0]); // /rep <Target name>
                }
                break;
            case 2:
                if (args[0].equalsIgnoreCase("give")) {
                    giveReputation(commandSender, args[1]); // /rep give <Target name>
                } else {
                    sendHelp(commandSender); // /rep target give
                }
                break;
            case 3:
                if (args[0].equalsIgnoreCase("player") && args[2].equalsIgnoreCase("reset")) {
                    resetPlayerReputation(commandSender, args[1]); // /rep player <Target name> reset
                } else {
                    sendHelp(commandSender); // /rep target reset player
                }
                break;
            case 4:
                if (args[0].equalsIgnoreCase("player") && args[2].equalsIgnoreCase("set")) {
                    setPlayerReputation(commandSender, args[1], args[3]); // /rep player <Target name> set <Amount>
                } else {
                    sendHelp(commandSender); // /rep target set player amount
                }
                break;
            default:
                sendHelp(commandSender); // /rep
                break;
        }
    }

    /*
    Отправить игроку сообщение со списком доступных команд плагина
     */
    private void sendHelp(CommandSender commandSender) {
        messagesConfig.playerSection().helpStrings().forEach(str -> commandSender.sendMessage(messageManager.parsePluginPlaceholders(str)));
        if (commandSender.hasPermission("reputation.admin.help")) {
            messagesConfig.adminSection().helpStrings().forEach(str -> commandSender.sendMessage(messageManager.parsePluginPlaceholders(str)));
        }
    }

    /*
    Отправить игроку информацию о его репутации
     */
    private void sendSelfInfo(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().notAPlayer()));
        } else {
            IGamePlayer gamePlayer = playersContainer.getCachedPlayerByUUID(((Player) commandSender).getUniqueId());
            commandSender.sendMessage(messageManager.parsePlaceholders(gamePlayer, messagesConfig.playerSection().selfInfo()));
        }
    }

    /*
    Отправить игроку информацию о репутации запрашиваемого игрока при его нахождении на сервере
     */
    private void sendPlayerInfo(CommandSender commandSender, String targetName) {
        Player targetPlayer = Bukkit.getPlayer(targetName);
        if (targetPlayer == null) {
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().playerIsOffline()));
        } else {
            IGamePlayer targetGamePlayer = playersContainer.getCachedPlayerByUUID(targetPlayer.getUniqueId());
            if (targetGamePlayer == null) {
                commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().playerNotInCache()));
            } else {
                commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.playerSection().playerInfo()));
            }
        }
    }

    /*
    Дать игроку при его нахождении на сервере очко репутации, если уже не было дано
     */
    private void giveReputation(CommandSender commandSender, String targetName) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().notAPlayer()));
        } else {
            if (commandSender.getName().equalsIgnoreCase(targetName)) {
                commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().cantSelf()));
            } else {
                IGamePlayer gamePlayer = playersContainer.getCachedPlayerByUUID(((Player) commandSender).getUniqueId());
                Player targetPlayer = Bukkit.getPlayer(targetName);
                if (targetPlayer == null) {
                    commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().playerIsOffline()));
                } else {
                    IGamePlayer targetGamePlayer = playersContainer.getCachedPlayerByUUID(targetPlayer.getUniqueId());
                    if (targetGamePlayer == null) {
                        commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().playerNotInCache()));
                    } else {
                        if (gamePlayer.getIDsWhomGaveReputation().contains(targetGamePlayer.getId())) {
                            commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.playerSection().alreadyGaveReputation()));
                        } else {
                            targetGamePlayer.setPlayerReputation(targetGamePlayer.getPlayerReputation() + 1);
                            gamePlayer.getIDsWhomGaveReputation().add(targetGamePlayer.getId());
                            database.saveAction(gamePlayer, targetGamePlayer);
                            commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.playerSection().gaveReputation()));
                        }
                    }
                }
            }
        }
    }

    /*
    Сбросить игроку очки репутации, сбросить кому он давал и кто давал ему
     */
    private void resetPlayerReputation(CommandSender commandSender, String targetName) {
        if (commandSender.hasPermission("reputation.admin.reset")) {
            Player targetPlayer = Bukkit.getPlayer(targetName);
            if (targetPlayer == null) {
                commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().playerIsOffline()));
            } else {
                IGamePlayer targetGamePlayer = playersContainer.getCachedPlayerByUUID(targetPlayer.getUniqueId());
                if (targetGamePlayer == null) {
                    commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().playerNotInCache()));
                } else {
                    targetGamePlayer.setPlayerReputation(0);
                    targetGamePlayer.setIDsWhomGaveReputation(new ArrayList<>());
                    playersContainer.getAllCachedPlayers().forEach(cachedPlayer -> cachedPlayer.getIDsWhomGaveReputation().remove(targetGamePlayer.getId()));
                    commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.adminSection().playerReset()));
                }
            }
        } else {
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.adminSection().noPermission()));
        }
    }

    /*
    Установить игроку очки репутации
     */
    private void setPlayerReputation(CommandSender commandSender, String targetName, String reputation) {
        if (commandSender.hasPermission("reputation.admin.set")) {
            Player targetPlayer = Bukkit.getPlayer(targetName);
            if (targetPlayer == null) {
                commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().playerIsOffline()));
            } else {
                IGamePlayer targetGamePlayer = playersContainer.getCachedPlayerByUUID(targetPlayer.getUniqueId());
                if (targetGamePlayer == null) {
                    commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().playerNotInCache()));
                } else {
                    if (reputation.chars().allMatch(Character::isDigit)) {
                        targetGamePlayer.setPlayerReputation(Long.parseLong(reputation));
                        commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.adminSection().playerSet()));
                    } else {
                        commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.adminSection().mustBeNumber()));
                    }
                }
            }
        } else {
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.adminSection().noPermission()));
        }
    }

    /*
    Перезагрузить файлы конфигурации плагина (config.yml, lang.yml)
     */
    private void reloadPlugin(CommandSender commandSender) {
        if (commandSender.hasPermission("reputation.admin.reload")) {
            messagesConfigManager.reloadConfig();
            mainConfigManager.reloadConfig();
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.adminSection().configsReloadedSuccessfully()));
        } else {
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.adminSection().noPermission()));
        }
    }
}
