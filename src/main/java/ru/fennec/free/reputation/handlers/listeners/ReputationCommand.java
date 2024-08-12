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
import ru.fennec.free.reputation.handlers.enums.OrderBy;
import ru.fennec.free.reputation.handlers.enums.UpdateAction;
import ru.fennec.free.reputation.handlers.events.ReputationPreUpdateEvent;
import ru.fennec.free.reputation.handlers.events.ReputationUpdatedEvent;
import ru.fennec.free.reputation.handlers.messages.MessageManager;
import ru.fennec.free.reputation.handlers.players.PlayersContainer;

import java.util.ArrayList;
import java.util.Arrays;
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

    public ReputationCommand(ReputationPlugin plugin, ConfigManager<MessagesConfig> messagesConfigManager, ConfigManager<MainConfig> mainConfigManager, IDatabase database,
                             PlayersContainer playersContainer, MessageManager messageManager) {
        super(plugin, "reputation");
        this.plugin = plugin;
        this.messagesConfigManager = messagesConfigManager;
        this.mainConfigManager = mainConfigManager;
        this.mainConfig = mainConfigManager.getConfigData();
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
                    case "help":
                    case "give":
                    case "take":
                    case "player": {
                        sendHelp(commandSender); // /rep help
                        break;
                    }
                    case "info":
                    case "me":
                    case "self": {
                        sendSelfInfo(commandSender); // /rep me
                        break;
                    }
                    case "reload": {
                        reloadPlugin(commandSender); // /rep reload
                        break;
                    }
                    case "top": {
                        sendTop(commandSender, OrderBy.DESC); // /rep top
                        break;
                    }
                    case "badtop": {
                        sendTop(commandSender, OrderBy.ASC); // /rep badtop
                        break;
                    }
                    case "reject": {
                        rejectReputation(commandSender); // /rep reject
                        break;
                    }
                    case "about": {
                        aboutPlugin(commandSender);
                        break;
                    }
                    default: {
                        sendPlayerInfo(commandSender, args[0]); // /rep <Target name>
                        break;
                    }
                }
                break;
            case 2:
                switch (args[0].toLowerCase()) {
                    case "give": {
                        giveReputation(commandSender, args[1]); // /rep give <Target name>
                        break;
                    }
                    case "take": {
                        takeReputation(commandSender, args[1]); // /rep take <Target name>
                        break;
                    }
                    case "top": {
                        sendTopOnline(commandSender, OrderBy.DESC); // /rep top online /rep top lalalal
                        break;
                    }
                    case "badtop": {
                        sendTopOnline(commandSender, OrderBy.ASC); // /rep badtop online /rep top lalalal
                        break;
                    }
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
                if (args[0].equalsIgnoreCase("player")) {
                    if (args[2].equalsIgnoreCase("set")) {
                        setPlayerReputation(commandSender, args[1], args[3]); // /rep player <Target name> set <Amount>
                    } else if (args[2].equalsIgnoreCase("add")) {
                        addPlayerReputation(commandSender, args[1], args[3]); // /rep player <Target name> add <Amount>
                    } else if (args[2].equalsIgnoreCase("remove")) {
                        removePlayerReputation(commandSender, args[1], args[3]); // /rep player <Target name> remove <Amount>
                    } else {
                        sendHelp(commandSender);
                    }
                } else {
                    sendHelp(commandSender); // /rep target set or add player amount
                }
                break;
            default:
                sendHelp(commandSender); // /rep
                break;
        }
    }

    /*
    Сообщение о версии плагина и его разработчике
     */
    private void aboutPlugin(CommandSender commandSender) {
        commandSender.sendMessage(messageManager.parsePluginPlaceholders("${prefix} &aПлагин Reputation от BuseSo (iBuseWinner). " +
                "Установлена версия " + plugin.getDescription().getVersion()));
        commandSender.sendMessage(messageManager.parsePluginPlaceholders("${prefix} &aСтраница плагина: https://spigotmc.ru/resources/124/"));
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

        if (!gamePlayer.acceptReputation()) {
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().youDeclinedReputation()));
            return;
        }

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

        if (!targetGamePlayer.acceptReputation()) {
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().playerDeclinedReputation()));
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

        if (!gamePlayer.acceptReputation()) {
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().youDeclinedReputation()));
            return;
        }

        IGamePlayer targetGamePlayer = playersContainer.getCachedPlayerByUUID(targetPlayer.getUniqueId());
        if (targetGamePlayer == null) {
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().playerNotInCache()));
            return;
        }

        if (!targetGamePlayer.acceptReputation()) {
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().playerDeclinedReputation()));
            return;
        }

        if (targetPlayer.getAddress().getAddress().equals(((Player) commandSender).getAddress().getAddress())) {
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().cantSelf()));
            return;
        }

        if (gamePlayer.getIDsWhomGaveReputation().contains(targetGamePlayer.getId())) {
            commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.playerSection().alreadyGaveReputation()));
            return;
        }

        if (mainConfig.oneReputationPerPlayer() && gamePlayer.getIDsWhomTookReputation().contains(targetGamePlayer.getId())) {
            commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.playerSection().alreadyTookReputation()));
            return;
        }

        AtomicLong maxReputationCanGive = new AtomicLong();
        mainConfig.maxReputation().forEach((permission, reputation) -> {
            if (commandSender.hasPermission("reputation.max." + permission)) maxReputationCanGive.set(reputation);
        });

        boolean canGive = false;
        if (maxReputationCanGive.get() == -1) canGive = true;
        else if (gamePlayer.getIDsWhomGaveReputation().size() < maxReputationCanGive.get()) canGive = true;

        if (canGive) {
            ReputationPreUpdateEvent reputationPreUpdateEvent = new ReputationPreUpdateEvent(targetGamePlayer, UpdateAction.INCREASE);
            Bukkit.getPluginManager().callEvent(reputationPreUpdateEvent);
            if (!reputationPreUpdateEvent.isCancelled()) {
                targetGamePlayer.setPlayerReputation(targetGamePlayer.getPlayerReputation() + 1);
                gamePlayer.getIDsWhomGaveReputation().add(targetGamePlayer.getId());
                database.saveAction(gamePlayer, targetGamePlayer, "INCREASE");
                commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.playerSection().gaveReputation()));
                targetPlayer.sendMessage(messageManager.parsePlaceholders(gamePlayer, messagesConfig.playerSection().youGotReputation()));
                ReputationUpdatedEvent reputationUpdatedEvent = new ReputationUpdatedEvent(targetGamePlayer, UpdateAction.INCREASE);
                Bukkit.getPluginManager().callEvent(reputationUpdatedEvent);
            }
        } else {
            commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.playerSection().maxReputation()));
        }
    }

    /*
    Дать игроку при его нахождении на сервере очко репутации, если уже не было дано
     */
    private void takeReputation(CommandSender commandSender, String targetName) {
        if (!mainConfig.takeReputation()) {
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().functionDisabled()));
            return;
        }

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

        if (!gamePlayer.acceptReputation()) {
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().youDeclinedReputation()));
            return;
        }

        IGamePlayer targetGamePlayer = playersContainer.getCachedPlayerByUUID(targetPlayer.getUniqueId());
        if (targetGamePlayer == null) {
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().playerNotInCache()));
            return;
        }

        if (!targetGamePlayer.acceptReputation()) {
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().playerDeclinedReputation()));
            return;
        }

        if (gamePlayer.getIDsWhomTookReputation().contains(targetGamePlayer.getId())) {
            commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.playerSection().alreadyTookReputation()));
            return;
        }

        if (mainConfig.oneReputationPerPlayer() && gamePlayer.getIDsWhomGaveReputation().contains(targetGamePlayer.getId())) {
            commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.playerSection().alreadyGaveReputation()));
            return;
        }

        AtomicLong maxReputationCanGive = new AtomicLong();
        mainConfig.maxReputation().forEach((permission, reputation) -> {
            if (commandSender.hasPermission("reputation.max." + permission)) maxReputationCanGive.set(reputation);
        });

        boolean canGive = false;
        if (maxReputationCanGive.get() == -1) canGive = true;
        else if (gamePlayer.getIDsWhomTookReputation().size() < maxReputationCanGive.get()) canGive = true;

        if (canGive) {
            ReputationPreUpdateEvent reputationPreUpdateEvent = new ReputationPreUpdateEvent(targetGamePlayer, UpdateAction.DECREASE);
            Bukkit.getPluginManager().callEvent(reputationPreUpdateEvent);
            if (!reputationPreUpdateEvent.isCancelled()) {
                targetGamePlayer.setPlayerReputation(targetGamePlayer.getPlayerReputation() - 1);
                gamePlayer.getIDsWhomTookReputation().add(targetGamePlayer.getId());
                database.saveAction(gamePlayer, targetGamePlayer, "DECREASE");
                commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.playerSection().tookReputation()));
                targetPlayer.sendMessage(messageManager.parsePlaceholders(gamePlayer, messagesConfig.playerSection().youGotNegativeReputation()));
                ReputationUpdatedEvent reputationUpdatedEvent = new ReputationUpdatedEvent(targetGamePlayer, UpdateAction.DECREASE);
                Bukkit.getPluginManager().callEvent(reputationUpdatedEvent);
            }
        } else {
            commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.playerSection().maxReputation()));
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

            ReputationPreUpdateEvent reputationPreUpdateEvent = new ReputationPreUpdateEvent(targetGamePlayer, UpdateAction.RESET);
            Bukkit.getPluginManager().callEvent(reputationPreUpdateEvent);
            if (!reputationPreUpdateEvent.isCancelled()) {
                targetGamePlayer.setPlayerReputation(mainConfig.defaultReputation());
                targetGamePlayer.setIDsWhomGaveReputation(new ArrayList<>());
                playersContainer.getAllCachedPlayers().forEach(cachedPlayer -> cachedPlayer.getIDsWhomGaveReputation().remove(targetGamePlayer.getId()));
                database.deleteAction(targetGamePlayer);
                commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.adminSection().playerReset()));
                ReputationUpdatedEvent reputationUpdatedEvent = new ReputationUpdatedEvent(targetGamePlayer, UpdateAction.RESET);
                Bukkit.getPluginManager().callEvent(reputationUpdatedEvent);
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
            boolean isNumber = reputation.matches("[+-]?[0-9]+");

            if (isNumber) {
                try {
                    ReputationPreUpdateEvent reputationPreUpdateEvent = new ReputationPreUpdateEvent(targetGamePlayer, UpdateAction.SET);
                    Bukkit.getPluginManager().callEvent(reputationPreUpdateEvent);
                    if (!reputationPreUpdateEvent.isCancelled()) {
                        targetGamePlayer.setPlayerReputation(Long.parseLong(reputation));
                        commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.adminSection().playerSet()));
                        ReputationUpdatedEvent reputationUpdatedEvent = new ReputationUpdatedEvent(targetGamePlayer, UpdateAction.SET);
                        Bukkit.getPluginManager().callEvent(reputationUpdatedEvent);
                    }
                } catch (NumberFormatException e) {
                    commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.adminSection().numberIsTooLong()));
                }
                return;
            }

            commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.adminSection().mustBeNumber()));
            return;
        }

        commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.adminSection().noPermission()));
    }

    /*
    Добавить игроку очки репутации
     */
    private void addPlayerReputation(CommandSender commandSender, String targetName, String reputation) {
        if (commandSender.hasPermission("reputation.admin.add")) {
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
            boolean isNumber = reputation.matches("[+-]?[0-9]+");

            if (isNumber) {
                try {
                    ReputationPreUpdateEvent reputationPreUpdateEvent = new ReputationPreUpdateEvent(targetGamePlayer, UpdateAction.ADD);
                    Bukkit.getPluginManager().callEvent(reputationPreUpdateEvent);
                    if (!reputationPreUpdateEvent.isCancelled()) {
                        targetGamePlayer.setPlayerReputation(targetGamePlayer.getPlayerReputation() + Long.parseLong(reputation));
                        commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.adminSection().playerSet()));
                        ReputationUpdatedEvent reputationUpdatedEvent = new ReputationUpdatedEvent(targetGamePlayer, UpdateAction.ADD);
                        Bukkit.getPluginManager().callEvent(reputationUpdatedEvent);
                    }
                } catch (NumberFormatException e) {
                    commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.adminSection().numberIsTooLong()));
                }
                return;
            }

            commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.adminSection().mustBeNumber()));
            return;
        }

        commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.adminSection().noPermission()));
    }

    /*
    Убрать у игрока очки репутации
     */
    private void removePlayerReputation(CommandSender commandSender, String targetName, String reputation) {
        if (commandSender.hasPermission("reputation.admin.remove")) {
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
            boolean isNumber = reputation.matches("[+-]?[0-9]+");

            if (isNumber) {
                try {
                    ReputationPreUpdateEvent reputationPreUpdateEvent = new ReputationPreUpdateEvent(targetGamePlayer, UpdateAction.REMOVE);
                    Bukkit.getPluginManager().callEvent(reputationPreUpdateEvent);
                    if (!reputationPreUpdateEvent.isCancelled()) {
                        targetGamePlayer.setPlayerReputation(targetGamePlayer.getPlayerReputation() + Long.parseLong(reputation));
                        commandSender.sendMessage(messageManager.parsePlaceholders(targetGamePlayer, messagesConfig.adminSection().playerSet()));
                        ReputationUpdatedEvent reputationUpdatedEvent = new ReputationUpdatedEvent(targetGamePlayer, UpdateAction.REMOVE);
                        Bukkit.getPluginManager().callEvent(reputationUpdatedEvent);
                    }
                } catch (NumberFormatException e) {
                    commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.adminSection().numberIsTooLong()));
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
            messagesConfigManager.reloadConfig(plugin.getLogger());
            mainConfigManager.reloadConfig(plugin.getLogger());
            plugin.updateConfigData();
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
    private void sendTop(CommandSender commandSender, OrderBy orderBy) {
        commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().topMessage()));
        for (int place = 0; place < mainConfig.topAmount(); place++) {
            try {
                String playerName = Bukkit.getOfflinePlayer(database.getTopGamePlayerUUIDByReputation(place + 1, orderBy)).getName();
                long playerReputation = database.getTopGamePlayerReputationByReputation(place + 1, orderBy);

                String message = messagesConfig.playerSection().topFormat();
                message = StaticReplacer.replacer()
                        .set("place", place + 1)
                        .set("player_name", playerName)
                        .set("player_reputation", playerReputation)
                        .apply(message);
                commandSender.sendMessage(messageManager.parsePluginPlaceholders(message));
            } catch (IllegalStateException | ArrayIndexOutOfBoundsException e) {
                break;
            } //Если в бд игроков меньше, чем показывает топ
        }
    }

    /*
    Отправить топ онлайн игроков по репутации
     */
    private void sendTopOnline(CommandSender commandSender, OrderBy orderBy) {
        commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().topMessage()));
        for (int place = 0; place < mainConfig.topAmount(); place++) {
            try {
                IGamePlayer gamePlayer = playersContainer.getTopGamePlayerByReputation(place, orderBy);
                String playerName = gamePlayer.getBukkitPlayer().getName();
                long playerReputation = gamePlayer.getPlayerReputation();

                String message = messagesConfig.playerSection().topFormat();
                message = StaticReplacer.replacer()
                        .set("place", place + 1)
                        .set("player_name", playerName)
                        .set("player_reputation", playerReputation)
                        .apply(message);
                commandSender.sendMessage(messageManager.parsePluginPlaceholders(message));
            } catch (IndexOutOfBoundsException e) {
                break;
            } //Если онлайн игроков меньше, чем показывает топ
        }
    }

    /*
    Отказаться от репутации или вернуться к ней
     */
    private void rejectReputation(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().notAPlayer()));
            return;
        }

        if (!mainConfig.rejectReputation()) {
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().rejectionDisabled()));
            return;
        }

        IGamePlayer gamePlayer = playersContainer.getCachedPlayerByUUID(((Player) commandSender).getUniqueId());
        if (gamePlayer.acceptReputation()) {
            //Reject
            gamePlayer.setAcceptReputation(false);
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().rejectReputation()));
        } else {
            //Remove rejection
            gamePlayer.setAcceptReputation(true);
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().removeRejectionReputation()));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        List<String> tab = new ArrayList<>();
        switch (args.length) {
            case 1:
                tab.addAll(Arrays.asList("help", "me", "top", "badtop", "give", "about"));
                if (mainConfig.takeReputation()) tab.add("take");
                if (mainConfig.rejectReputation()) tab.add("reject");
                if (commandSender.hasPermission("reputation.admin.reload")) tab.add("reload");
                if (commandSender.hasPermission("reputation.admin.set") ||
                        commandSender.hasPermission("reputation.admin.reset") ||
                        commandSender.hasPermission("reputation.admin.add")) tab.add("player");
                Bukkit.getOnlinePlayers().forEach(player -> tab.add(player.getName()));
                break;
            case 2:
                if (args[0].equalsIgnoreCase("top")) tab.add("online");
                if (args[0].equalsIgnoreCase("badtop")) tab.add("online");
                if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("take"))
                    Bukkit.getOnlinePlayers().forEach(player -> tab.add(player.getName()));
                if (args[0].equalsIgnoreCase("player") &&
                        (commandSender.hasPermission("reputation.admin.set") ||
                                commandSender.hasPermission("reputation.admin.reset") ||
                                commandSender.hasPermission("reputation.admin.add")))
                    Bukkit.getOnlinePlayers().forEach(player -> tab.add(player.getName()));
                break;
            case 3:
                if (args[0].equalsIgnoreCase("player") && args.length > 1 && args[1].length() > 0) {
                    if (commandSender.hasPermission("reputation.admin.set")) tab.add("set");
                    if (commandSender.hasPermission("reputation.admin.reset")) tab.add("reset");
                    if (commandSender.hasPermission("reputation.admin.add")) tab.add("add");
                }
                break;
        }
        return tab;
    }
}
