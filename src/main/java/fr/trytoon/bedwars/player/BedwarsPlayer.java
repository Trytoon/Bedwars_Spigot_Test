package fr.trytoon.bedwars.player;

import fr.trytoon.bedwars.teams.BedwarsTeam;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

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

    public Player getPlayer() {
        return player;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getBedDestroyed() {
        return bedDestroyed;
    }

    public void setBedDestroyed(int bedDestroyed) {
        this.bedDestroyed = bedDestroyed;
    }

    public BedwarsTeam getTeam() {
        return team;
    }

    public void setTeam(BedwarsTeam team) {
        this.team = team;
    }
}
