package fr.trytoon.bedwars.events;

import fr.trytoon.bedwars.teams.BedwarsTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerTeamJoinEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final BedwarsTeam bedwarsTeam;

    public PlayerTeamJoinEvent(Player player, BedwarsTeam bedwarsTeam) {
        this.player = player;
        this.bedwarsTeam = bedwarsTeam;
    }


    public Player getPlayer() {
        return player;
    }

    public BedwarsTeam getTeam() {
        return bedwarsTeam;
    }


    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

