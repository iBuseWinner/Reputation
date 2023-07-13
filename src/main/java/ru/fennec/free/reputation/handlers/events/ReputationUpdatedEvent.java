package ru.fennec.free.reputation.handlers.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ru.fennec.free.reputation.handlers.enums.UpdateAction;
import ru.fennec.free.reputation.common.interfaces.IGamePlayer;

/***
 * Ивент вызывается из ReputationCommand при изменении очков репутации игрока
 * IGamePlayer - игрок, у которого изменяется репутация
 * UpdateAction - действие, которое произошло с репутацией игрока:
 *     INCREASE - выдал другой игрок, DECREASE - отнял другой игрок, (RE)SET - админской командой
 */
public class ReputationUpdatedEvent extends Event {
    private final IGamePlayer gamePlayer;
    private final UpdateAction updateAction;

    private static final HandlerList handlerList = new HandlerList();

    public ReputationUpdatedEvent(IGamePlayer gamePlayer, UpdateAction updateAction) {
        this.gamePlayer = gamePlayer;
        this.updateAction = updateAction;
    }

    public IGamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public UpdateAction getUpdateAction() {
        return updateAction;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
