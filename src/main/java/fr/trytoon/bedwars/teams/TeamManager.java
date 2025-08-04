package fr.trytoon.bedwars.teams;

import fr.trytoon.bedwars.BedwarsConstants;
import fr.trytoon.bedwars.BedwarsPlugin;
import fr.trytoon.bedwars.player.BedwarsPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.material.Bed;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Bukkit.getServer;

public class TeamManager {

    BedwarsPlugin plugin;

    Map<String, BedwarsTeam> teams = new HashMap<>();

    public TeamManager(BedwarsPlugin plugin) {
        this.plugin = plugin;
        loadTeams();
    }

    public void loadTeams() {
        FileConfiguration teamsConfig = plugin.getYAMLConfigurationFromPath(BedwarsConstants.TEAM_FILE_PATH);

        for (String teamName : teamsConfig.getKeys(false)) {
            int players = teamsConfig.getInt(teamName + ".players");
            int spawnX = teamsConfig.getInt(teamName + ".spawn_coordinates.x");
            int spawnY = teamsConfig.getInt(teamName + ".spawn_coordinates.y");
            int spawnZ = teamsConfig.getInt(teamName + ".spawn_coordinates.z");
            Location spawnLocation = new Location(getServer().getWorld(BedwarsConstants.TEST_WORLD), spawnX, spawnY, spawnZ);

            int bedX = teamsConfig.getInt(teamName + ".bed_coordinates.x");
            int bedY = teamsConfig.getInt(teamName + ".bed_coordinates.y");
            int bedZ = teamsConfig.getInt(teamName + ".bed_coordinates.z");
            Location bedLocation = new Location(getServer().getWorld(BedwarsConstants.TEST_WORLD), bedX, bedY, bedZ);

            TeamColor color;
            if (teamsConfig.contains(teamName + ".color")) {
                String colorName = teamsConfig.getString(teamName + ".color");
                color = new TeamColor(colorName);
            }
            else {
                color = new TeamColor("WHITE");
            }

            BedwarsTeam bedwarsTeam = new BedwarsTeam(teamName, spawnLocation, bedLocation, players, color);
            teams.put(teamName, bedwarsTeam);
        }
    }

    public void addPlayerToTeam(BedwarsTeam bedwarsTeam, BedwarsPlayer bedwarsPlayer) {
        Player player = bedwarsPlayer.getPlayer();

        if (bedwarsTeam != null && player != null) {
            String teamName = bedwarsTeam.getName();

            if (bedwarsTeam.getPlayersCount() < bedwarsTeam.getMaxMembers()) {
                bedwarsTeam.addToTeam(bedwarsPlayer);
                bedwarsPlayer.setTeam(bedwarsTeam);

                player.sendMessage("Vous avez officiellement rejoint la team: " + teamName);
            }
            else {
                player.sendMessage("Erreur ! L'équipe " + teamName + " est complète.");
            }
        }
    }

    public void removePlayerFromTeam(BedwarsTeam team, BedwarsPlayer player) {
        if (team.hasPlayer(player)) {
            team.removeFromTeam(player);
        }
    }


    public boolean deleteTeam(String teamName) {
        FileConfiguration teamsConfig = plugin.getYAMLConfigurationFromPath(BedwarsConstants.TEAM_FILE_PATH);

        if (teamsConfig != null) {

            if (teamsConfig.contains(teamName)) {
                teamsConfig.set(teamName, null);

                try {
                    teamsConfig.save(plugin.getConfigurationFileFromPath(BedwarsConstants.TEAM_FILE_PATH));
                } catch (IOException e) {
                   return false;
                }
            }

            teams.remove(teamName);

            return true;
        }

        return false;
    }

    public boolean createTeam(String name, int members, Location spawnLocation) {
        FileConfiguration teamsConfig = plugin.getYAMLConfigurationFromPath(BedwarsConstants.TEAM_FILE_PATH);

        String teamName = name;
        teamsConfig.set(teamName + ".players", members);
        teamsConfig.set(teamName + ".spawn_coordinates.x", spawnLocation.getBlockX());
        teamsConfig.set(teamName + ".spawn_coordinates.y", spawnLocation.getBlockY());
        teamsConfig.set(teamName + ".spawn_coordinates.z", spawnLocation.getBlockZ());
        teamsConfig.set(teamName + ".bed_coordinates.x", 0);
        teamsConfig.set(teamName + ".bed_coordinates.y", 0);
        teamsConfig.set(teamName + ".bed_coordinates.z", 0);
        teamsConfig.set(teamName + ".color", "white");

        try {
            teamsConfig.save(plugin.getConfigurationFileFromPath(BedwarsConstants.TEAM_FILE_PATH));
        } catch (IOException e) {
            return false;
        }

        BedwarsTeam bedwarsTeam = new BedwarsTeam(teamName, spawnLocation, new Location(spawnLocation.getWorld(), 0, 0, 0), members, new TeamColor("white"));
        teams.put(teamName, bedwarsTeam);

        return true;
    }


    public BedwarsTeam findTeamByBed(Location location) {
        for (BedwarsTeam team : teams.values()) {
            if (team.getBedPosition().equals(location) || location.equals(findOtherBedPartLocation(team.getBedPosition()))) {
                return team;
            }
        }

        return null;
    }

    public Location findOtherBedPartLocation(Location bedHeadLocation) {
        Block block = bedHeadLocation.getBlock();

        if (block.getType() != Material.BED_BLOCK) {
            return bedHeadLocation;
        }

        Bed bed = (Bed) block.getState().getData();
        BlockFace facing = bed.getFacing();
        boolean isHead = bed.isHeadOfBed();

        Location otherPartLocation = bedHeadLocation.clone();
        int dx = 0, dz = 0;

        switch (facing) {
            case NORTH: dz = isHead ? 1 : -1; break;
            case SOUTH: dz = isHead ? -1 : 1; break;
            case EAST:  dx = isHead ? -1 : 1; break;
            case WEST:  dx = isHead ? 1 : -1; break;
            default: return bedHeadLocation;
        }

        otherPartLocation.add(dx, 0, dz);
        return otherPartLocation;
    }

    public Map<String, BedwarsTeam> getTeams() {
        return teams;
    }

    public BedwarsTeam getTeamByName(String name) {
        return teams.get(name);
    }
}
