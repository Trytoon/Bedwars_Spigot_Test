package fr.trytoon.bedwars.teams;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BedwarsTeam {
    private final String name;
    private final List<Player> players;
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

    public void addToTeam(Player p) {
        this.players.add(p);
    }

    public void removeFromTeam(Player p) {
        this.players.remove(p);

    }

    public String getName() {
        return name;
    }

    public List<Player> getPlayers() {
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

    public boolean hasPlayer(Player player) {
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

    public void setDeadMembers(int deadMembers) {
        this.deadMembers = deadMembers;
    }
}
