package fr.trytoon.bedwars.teams;

import fr.trytoon.bedwars.player.BedwarsPlayer;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class BedwarsTeam {
    private final String name;
    private final List<BedwarsPlayer> players;
    private final int maxMembers;

    private int deadMembers;

    private final Location spawn;
    private final Location bedPosition;

    private final TeamColor teamColor;
    boolean isBedBroken;

    public BedwarsTeam(String name, Location spawn, Location bedPosition, int maxMembers, TeamColor teamColor) {
        this.name = name;
        this.spawn = spawn;
        this.bedPosition = bedPosition;
        this.maxMembers = maxMembers;
        this.teamColor = teamColor;

        this.isBedBroken = false;
        this.players = new ArrayList<>();
        this.deadMembers = 0;
    }

    public void addToTeam(BedwarsPlayer player) {
        this.players.add(player);
    }

    public void removeFromTeam(BedwarsPlayer player) {
        this.players.remove(player);

    }

    public String getName() {
        return name;
    }

    public List<BedwarsPlayer> getPlayers() {
        return players;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public Location getSpawn() {
        return spawn;
    }

    public Location getBedPosition() {
        return bedPosition;
    }

    public int getPlayersCount() {
        return getPlayers().size();
    }

    public TeamColor getTeamColor() {
        return this.teamColor;
    }

    public boolean isBedBroken() {
        return isBedBroken;
    }

    public void setBedBroken(boolean bedBroken) {
        isBedBroken = bedBroken;
    }

    public boolean hasPlayer(BedwarsPlayer player) {
        return getPlayers().contains(player);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BedwarsTeam otherTeam) {
            return this.getName().equals(otherTeam.getName());
        }

        return false;
    }

    public int getDeadMembers() {
        return deadMembers;
    }

    public void incrementDeadPlayers() {
        this.deadMembers += 1;
    }
}
