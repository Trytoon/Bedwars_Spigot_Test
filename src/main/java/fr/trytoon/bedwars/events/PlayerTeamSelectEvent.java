package fr.trytoon.bedwars.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerTeamSelectEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final String teamName;

    public PlayerTeamSelectEvent(Player player, String teamName) {
        this.player = player;
        this.teamName = teamName;
    }


    public Player getPlayer() {
        return player;
    }


    public String getTeamName() {
        return teamName;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

