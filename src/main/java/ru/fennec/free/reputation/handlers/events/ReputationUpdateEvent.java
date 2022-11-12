package ru.fennec.free.reputation.handlers.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ru.fennec.free.reputation.common.enums.UpdateAction;
import ru.fennec.free.reputation.common.interfaces.IGamePlayer;

public class ReputationUpdateEvent extends Event implements Cancellable {
    private final IGamePlayer gamePlayer;
    private final UpdateAction updateAction;

    private boolean cancelled;

    private static final HandlerList handlerList = new HandlerList();

    public ReputationUpdateEvent(IGamePlayer gamePlayer, UpdateAction updateAction) {
        this.gamePlayer = gamePlayer;
        this.updateAction = updateAction;
        this.cancelled = false;
    }

    public IGamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public UpdateAction getUpdateAction() {
        return updateAction;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
