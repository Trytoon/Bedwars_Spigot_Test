package fr.trytoon.bedwars.events;

import fr.trytoon.bedwars.player.BedwarsPlayer;
import fr.trytoon.bedwars.teams.BedwarsTeam;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerTeamChangeEvent extends Event{
    private static final HandlerList handlers = new HandlerList();

    private final BedwarsPlayer bedwarsPlayer;
    private final BedwarsTeam teamBefore;
    private final BedwarsTeam teamAfter;

    public PlayerTeamChangeEvent(BedwarsPlayer bedwarsPlayer, BedwarsTeam teamBefore, BedwarsTeam teamAfter) {
        this.bedwarsPlayer = bedwarsPlayer;
        this.teamBefore = teamBefore;
        this.teamAfter = teamAfter;
    }


    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public BedwarsTeam getTeamBefore() {
        return teamBefore;
    }

    public BedwarsPlayer getBedwarsPlayer() {
        return bedwarsPlayer;
    }

    public BedwarsTeam getTeamAfter() {
        return teamAfter;
    }

}

