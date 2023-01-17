package ru.fennec.free.reputation.handlers.listeners;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.fennec.free.reputation.ReputationPlugin;
import ru.fennec.free.reputation.common.abstracts.AbstractCommand;
import ru.fennec.free.reputation.common.configs.ConfigManager;
import ru.fennec.free.reputation.common.interfaces.IDatabase;
import ru.fennec.free.reputation.common.interfaces.IGamePlayer;
import ru.fennec.free.reputation.common.replacers.StaticReplacer;
import ru.fennec.free.reputation.handlers.database.configs.MainConfig;
import ru.fennec.free.reputation.handlers.database.configs.MessagesConfig;
import ru.fennec.free.reputation.handlers.enums.UpdateAction;
import ru.fennec.free.reputation.handlers.events.ReputationUpdateEvent;
import ru.fennec.free.reputation.handlers.messages.MessageManager;
import ru.fennec.free.reputation.handlers.players.PlayersContainer;
import ru.fennec.free.reputation.handlers.players.TitlesHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class ReputationCommand extends AbstractCommand {

    private final ReputationPlugin plugin;
    private final ConfigManager<MessagesConfig> messagesConfigManager;
    private final ConfigManager<MainConfig> mainConfigManager;
    private MainConfig mainConfig;
    private MessagesConfig messagesConfig;
    private final IDatabase database;
    private final PlayersContainer playersContainer;
    private final MessageManager messageManager;
    private final TitlesHandler titlesHandler;

    public ReputationCommand(ReputationPlugin plugin, ConfigManager<MessagesConfig> messagesConfigManager, ConfigManager<MainConfig> mainConfigManager, IDatabase database,
                             PlayersContainer playersContainer, MessageManager messageManager, TitlesHandler titlesHandler) {
        super(plugin, "reputation");
        this.plugin = plugin;
        this.messagesConfigManager = messagesConfigManager;
        this.mainConfigManager = mainConfigManager;
        this.mainConfig = mainConfigManager.getConfigData();
        this.messagesConfig = messagesConfigManager.getConfigData();
        this.database = database;
        this.playersContainer = playersContainer;
        this.messageManager = messageManager;
        this.titlesHandler = titlesHandler;
    }

    @Override
    public void execute(CommandSender commandSender, String label, String[] args) {
        switch (args.length) {
            case 1:
                switch (args[0].toLowerCase()) {
                    case "help" -> sendHelp(commandSender); // /rep help
                    case "info", "me", "self" -> sendSelfInfo(commandSender); // /rep me
                    case "reload" -> reloadPlugin(commandSender); // /rep reload
                    case "top" -> sendTop(commandSender);
                    default -> sendPlayerInfo(commandSender, args[0]); // /rep <Target name>
                }
                break;
            case 2:
                switch (args[0].toLowerCase()) {
                    case "give" -> giveReputation(commandSender, args[1]); // /rep give <Target name>
                    case "top" -> sendTopOnline(commandSender); // /rep top online /rep top lalalal
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
            return;
        }

        IGamePlayer gamePlayer = playersContainer.getCachedPlayerByUUID(((Player) commandSender).getUniqueId());
        commandSender.sendMessage(messageManager.parsePlaceholders(gamePlayer, messagesConfig.playerSection().selfInfo()));
    }

    /*
    Отправить игроку информацию о репутации запрашиваемого игрока при его нахождении на сервере
     */
    private void sendPlayerInfo(CommandSender commandSender, String targetName) {
        Player targetPlayer = Bukkit.getPlayer(targetName);
        if (targetPlayer == null) {
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().playerIsOffline()));
            return;
        }

        IGamePlayer targetGamePlayer = playersContainer.getCachedPlayerByUUID(targetPlayer.getUniqueId());
        if (targetGamePlayer == null) {
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().playerNotInCache()));
            return;
        }

        commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.playerSection().playerInfo()));
    }

    /*
    Дать игроку при его нахождении на сервере очко репутации, если уже не было дано
     */
    private void giveReputation(CommandSender commandSender, String targetName) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().notAPlayer()));
            return;
        }

        if (commandSender.getName().equalsIgnoreCase(targetName)) {
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().cantSelf()));
            return;
        }

        IGamePlayer gamePlayer = playersContainer.getCachedPlayerByUUID(((Player) commandSender).getUniqueId());
        Player targetPlayer = Bukkit.getPlayer(targetName);
        if (targetPlayer == null) {
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().playerIsOffline()));
            return;
        }

        IGamePlayer targetGamePlayer = playersContainer.getCachedPlayerByUUID(targetPlayer.getUniqueId());
        if (targetGamePlayer == null) {
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().playerNotInCache()));
            return;
        }

        if (gamePlayer.getIDsWhomGaveReputation().contains(targetGamePlayer.getId())) {
            commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.playerSection().alreadyGaveReputation()));
            return;
        }

        AtomicLong maxReputationCanGive = new AtomicLong();
        mainConfig.maxReputation().forEach((permission, reputation) -> {
            if (commandSender.hasPermission("reputation.max."+permission)) maxReputationCanGive.set(reputation);
        });

        boolean canGive = false;
        if (maxReputationCanGive.get() == -1) canGive = true;
        else if (gamePlayer.getIDsWhomGaveReputation().size() < maxReputationCanGive.get()) canGive = true;

        if (canGive) {
            ReputationUpdateEvent reputationUpdateEvent = new ReputationUpdateEvent(targetGamePlayer, UpdateAction.INCREASE);
            Bukkit.getPluginManager().callEvent(reputationUpdateEvent);
            if (!reputationUpdateEvent.isCancelled()) {
                targetGamePlayer.setPlayerReputation(targetGamePlayer.getPlayerReputation() + 1);
                gamePlayer.getIDsWhomGaveReputation().add(targetGamePlayer.getId());
                database.saveAction(gamePlayer, targetGamePlayer);
                commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.playerSection().gaveReputation()));
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
                return;
            }

            IGamePlayer targetGamePlayer = playersContainer.getCachedPlayerByUUID(targetPlayer.getUniqueId());
            if (targetGamePlayer == null) {
                commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().playerNotInCache()));
                return;
            }

            ReputationUpdateEvent reputationUpdateEvent = new ReputationUpdateEvent(targetGamePlayer, UpdateAction.RESET);
            Bukkit.getPluginManager().callEvent(reputationUpdateEvent);
            if (!reputationUpdateEvent.isCancelled()) {
                targetGamePlayer.setPlayerReputation(0);
                targetGamePlayer.setIDsWhomGaveReputation(new ArrayList<>());
                playersContainer.getAllCachedPlayers().forEach(cachedPlayer -> cachedPlayer.getIDsWhomGaveReputation().remove(targetGamePlayer.getId()));
                database.deleteAction(targetGamePlayer);

                commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.adminSection().playerReset()));
            }
            return;
        }

        commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.adminSection().noPermission()));
    }

    /*
    Установить игроку очки репутации
     */
    private void setPlayerReputation(CommandSender commandSender, String targetName, String reputation) {
        if (commandSender.hasPermission("reputation.admin.set")) {
            Player targetPlayer = Bukkit.getPlayer(targetName);
            if (targetPlayer == null) {
                commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().playerIsOffline()));
                return;
            }

            IGamePlayer targetGamePlayer = playersContainer.getCachedPlayerByUUID(targetPlayer.getUniqueId());
            if (targetGamePlayer == null) {
                commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().playerNotInCache()));
                return;
            }
            if (reputation.chars().allMatch(Character::isDigit)) {
                ReputationUpdateEvent reputationUpdateEvent = new ReputationUpdateEvent(targetGamePlayer, UpdateAction.SET);
                Bukkit.getPluginManager().callEvent(reputationUpdateEvent);
                if (!reputationUpdateEvent.isCancelled()) {
                    targetGamePlayer.setPlayerReputation(Long.parseLong(reputation));

                    commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.adminSection().playerSet()));
                }
                return;
            }

            commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.adminSection().mustBeNumber()));
            return;
        }

        commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.adminSection().noPermission()));
    }

    /*
    Перезагрузить файлы конфигурации плагина (config.yml, lang.yml)
     */
    private void reloadPlugin(CommandSender commandSender) {
        if (commandSender.hasPermission("reputation.admin.reload")) {
            messagesConfigManager.reloadConfig();
            mainConfigManager.reloadConfig();
            plugin.updateConfigData(mainConfigManager, messagesConfigManager);
            mainConfig = mainConfigManager.getConfigData();
            messagesConfig = messagesConfigManager.getConfigData();
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.adminSection().configsReloadedSuccessfully()));
            return;
        }

        commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.adminSection().noPermission()));
    }

    /*
    Отправить топ игроков по репутации
     */
    private void sendTop(CommandSender commandSender) {
        commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().topMessage()));
        for (int place = 0; place < mainConfig.topAmount(); place++) {
            try {
                String playerName = Bukkit.getOfflinePlayer(database.getTopGamePlayerUUIDByReputation(place + 1)).getName();
                long playerReputation = database.getTopGamePlayerReputationByReputation(place + 1);

                String message = messagesConfig.playerSection().topFormat();
                message = StaticReplacer.replacer()
                        .set("place", place + 1)
                        .set("player_name", playerName)
                        .set("player_reputation", playerReputation)
                        .apply(message);
                commandSender.sendMessage(messageManager.parsePluginPlaceholders(message));
            } catch (IllegalStateException e) {
                break;
            } //Если в бд игроков меньше, чем показывает топ
        }
    }

    /*
    Отправить топ онлайн игроков по репутации
     */
    private void sendTopOnline(CommandSender commandSender) {
        commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().topMessage()));
        for (int place = 0; place < mainConfig.topAmount(); place++) {
            try {
                IGamePlayer gamePlayer = playersContainer.getTopGamePlayerByReputation(place);
                String playerName = gamePlayer.getBukkitPlayer().getName();
                long playerReputation = gamePlayer.getPlayerReputation();

                String message = messagesConfig.playerSection().topFormat();
                message = StaticReplacer.replacer()
                        .set("place", place + 1)
                        .set("player_name", playerName)
                        .set("player_reputation", playerReputation)
                        .apply(message);
                commandSender.sendMessage(messageManager.parsePluginPlaceholders(message));
            } catch (ArrayIndexOutOfBoundsException e) {
                break;
            } //Если онлайн игроков меньше, чем показывает топ
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        List<String> tab = new ArrayList<>();
        switch (args.length) {
            case 1:
                tab.addAll(List.of("help", "me", "self", "info", "top"));
                tab.add("give");
                if (commandSender.hasPermission("reputation.admin.reload")) tab.add("reload");
                if (commandSender.hasPermission("reputation.admin.set") ||
                        commandSender.hasPermission("reputation.admin.reset")) tab.add("player");
                Bukkit.getOnlinePlayers().forEach(player -> tab.add(player.getName()));
            case 2:
                if (args[0].equalsIgnoreCase("top")) tab.add("online");
                if (args[0].equalsIgnoreCase("give"))
                    Bukkit.getOnlinePlayers().forEach(player -> tab.add(player.getName()));
                if (args[0].equalsIgnoreCase("player") &&
                        (commandSender.hasPermission("reputation.admin.set") || commandSender.hasPermission("reputation.admin.reset")))
                    Bukkit.getOnlinePlayers().forEach(player -> tab.add(player.getName()));
            case 3:
                if (args[0].equalsIgnoreCase("player") && args.length > 1 && args[1].length() > 0) {
                    if (commandSender.hasPermission("reputation.admin.set")) tab.add("set");
                    if (commandSender.hasPermission("reputation.admin.reset")) tab.add("reset");
                }
        }
        return tab;
    }
}
