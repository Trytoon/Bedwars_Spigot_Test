package fr.trytoon.bedwars.events;

import fr.trytoon.bedwars.game.GameState;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStatusChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final GameState stateBefore;
    private final GameState stateAfter;

    public GameStatusChangeEvent(GameState stateBefore, GameState stateAfter) {
        this.stateBefore = stateBefore;
        this.stateAfter = stateAfter;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GameState getStateBefore() {
        return stateBefore;
    }

    public GameState getStateAfter() {
        return stateAfter;
    }
}
