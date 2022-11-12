package ru.fennec.free.reputation.handlers.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ru.fennec.free.reputation.common.enums.SaveReason;
import ru.fennec.free.reputation.common.interfaces.IGamePlayer;

public class ReputationSaveEvent extends Event implements Cancellable {
    private final IGamePlayer gamePlayer;
    private final SaveReason saveReason;

    private boolean cancelled;

    private static final HandlerList handlerList = new HandlerList();

    public ReputationSaveEvent(IGamePlayer gamePlayer, SaveReason saveReason) {
        this.gamePlayer = gamePlayer;
        this.saveReason = saveReason;
        this.cancelled = false;
    }

    public IGamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public SaveReason getSaveReason() {
        return saveReason;
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
