package fr.trytoon.bedwars.player;

import fr.trytoon.bedwars.teams.BedwarsTeam;
import org.bukkit.entity.Player;

public class BedwarsPlayer {

    final Player player;
    int deaths;
    int kills;
    int bedDestroyed;

    BedwarsTeam team;

    public BedwarsPlayer(Player p) {
        this.player = p;
        deaths = 0;
        kills = 0;
        bedDestroyed = 0;
    }

    public void incrementDeaths() {
        this.deaths += 1;
    }

    public Player getPlayer() {
        return player;
    }

    public int getKills() {
        return kills;
    }

    public int getBedDestroyed() {
        return bedDestroyed;
    }

    public BedwarsTeam getTeam() {
        return team;
    }

    public void setTeam(BedwarsTeam team) {
        this.team = team;
    }
}
