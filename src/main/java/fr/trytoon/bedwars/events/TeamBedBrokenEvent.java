package fr.trytoon.bedwars.events;

import fr.trytoon.bedwars.teams.BedwarsTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeamBedBrokenEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final BedwarsTeam bedwarsTeam;

    private final Player breaker;

    public TeamBedBrokenEvent(BedwarsTeam bedwarsTeam, Player breaker) {
        this.bedwarsTeam = bedwarsTeam;
        this.breaker = breaker;
    }


    public BedwarsTeam getTeam() {
        return bedwarsTeam;
    }

    public Player getBreaker() {
        return breaker;
    }


    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

