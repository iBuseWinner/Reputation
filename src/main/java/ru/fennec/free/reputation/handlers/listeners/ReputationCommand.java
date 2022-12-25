package ru.fennec.free.reputation.handlers.listeners;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.fennec.free.reputation.common.abstracts.AbstractCommand;
import ru.fennec.free.reputation.common.interfaces.IDatabase;
import ru.fennec.free.reputation.common.interfaces.IGamePlayer;
import ru.fennec.free.reputation.handlers.database.configs.MessagesConfig;
import ru.fennec.free.reputation.handlers.messages.MessageManager;
import ru.fennec.free.reputation.handlers.players.PlayersContainer;

public class ReputationCommand extends AbstractCommand {

    private final MessagesConfig messagesConfig;
    private final IDatabase database;
    private final PlayersContainer playersContainer;
    private final MessageManager messageManager;

    public ReputationCommand(Plugin plugin, MessagesConfig messagesConfig, IDatabase database,
                             PlayersContainer playersContainer, MessageManager messageManager) {
        super(plugin, "reputation");
        this.messagesConfig = messagesConfig;
        this.database = database;
        this.playersContainer = playersContainer;
        this.messageManager = messageManager;
    }

    @Override
    public void execute(CommandSender commandSender, String label, String[] args) {
        switch (args.length) {
            case 0:
                sendHelp(commandSender);
                break;
            case 1:
                switch (args[0].toLowerCase()) {
                    case "help" -> sendHelp(commandSender);
                    case "info", "me", "self" -> sendSelfInfoMessage(commandSender);
                    case "reload" -> reloadPlugin(commandSender);
                }
        }
    }

    private void sendHelp(CommandSender commandSender) {
        messagesConfig.playerSection().helpStrings().forEach(str -> commandSender.sendMessage(messageManager.parsePluginPlaceholders(str)));
        if (commandSender.hasPermission("reputation.admin.help")) {
            messagesConfig.adminSection().helpStrings().forEach(str -> commandSender.sendMessage(messageManager.parsePluginPlaceholders(str)));
        }
    }

    private void sendSelfInfoMessage(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(messageManager.parsePluginPlaceholders(messagesConfig.playerSection().notAPlayer()));
        } else {
            IGamePlayer gamePlayer = playersContainer.getCachedPlayerByUUID(((Player) commandSender).getUniqueId());
            commandSender.sendMessage(messageManager.parsePlaceholders(gamePlayer, messagesConfig.playerSection().selfInfo()));
        }
    }

    private void reloadPlugin(CommandSender commandSender) {

    }
}
