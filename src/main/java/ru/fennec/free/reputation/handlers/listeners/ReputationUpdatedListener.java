package ru.fennec.free.reputation.handlers.listeners;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.fennec.free.reputation.common.configs.ConfigManager;
import ru.fennec.free.reputation.common.interfaces.IDatabase;
import ru.fennec.free.reputation.common.interfaces.IGamePlayer;
import ru.fennec.free.reputation.handlers.database.configs.MainConfig;
import ru.fennec.free.reputation.handlers.enums.UpdateAction;
import ru.fennec.free.reputation.handlers.events.ReputationUpdatedEvent;
import ru.fennec.free.reputation.handlers.messages.MessageManager;

public class ReputationUpdatedListener implements Listener {

    private MainConfig mainConfig;
    private final IDatabase database;
    private final MessageManager messageManager;

    public ReputationUpdatedListener(ConfigManager<MainConfig> mainConfigManager, IDatabase database, MessageManager messageManager) {
        this.mainConfig = mainConfigManager.getConfigData();
        this.database = database;
        this.messageManager = messageManager;
    }

    @EventHandler
    private void onReputationUpdated(ReputationUpdatedEvent event) {
        IGamePlayer gamePlayer = event.getGamePlayer();
        UpdateAction updateAction = event.getUpdateAction();
        if (updateAction.equals(UpdateAction.INCREASE) || updateAction.equals(UpdateAction.DECREASE) || updateAction.equals(UpdateAction.SET)) {
            long reputation = gamePlayer.getPlayerReputation();
            MainConfig.ReputationCommands.ReputationNeeded selectedReputation = null;
            for (MainConfig.ReputationCommands.ReputationNeeded reputationNeeded : mainConfig.commands().needs()) {
                if (reputationNeeded.minReputation() == reputation) {
                    selectedReputation = reputationNeeded;
                }
            }
            if (selectedReputation == null) {
                return;
            }
            if (!selectedReputation.oneTime() || !database.isUsedCommand(gamePlayer, selectedReputation.id())) {
                selectedReputation.commands().forEach(command -> {
                    String cmd = command;
                    CommandSender commandSender = gamePlayer.getBukkitPlayer();
                    if (command.startsWith("console!")) {
                        commandSender = Bukkit.getConsoleSender();
                        cmd = cmd.substring("console!".length());
                    }
                    Bukkit.dispatchCommand(commandSender, messageManager.parsePlaceholders(gamePlayer, cmd));
                });
                if (selectedReputation.oneTime()) {
                    database.saveCommand(gamePlayer, selectedReputation.id());
                }
            }
        }
    }

    public void updateConfigData(ConfigManager<MainConfig> mainConfigManager) {
        this.mainConfig = mainConfigManager.getConfigData();
    }

}
