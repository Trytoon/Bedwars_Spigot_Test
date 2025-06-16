package fr.trytoon.bedwars.teams;

import com.connorlinfoot.titleapi.TitleAPI;
import fr.trytoon.bedwars.BedwarsConstants;
import fr.trytoon.bedwars.BedwarsPlugin;
import fr.trytoon.bedwars.events.BedBrokenEvent;
import fr.trytoon.bedwars.events.PlayerTeamJoinEvent;
import fr.trytoon.bedwars.events.PlayerTeamSelectEvent;
import fr.trytoon.bedwars.game.GameState;
import fr.trytoon.bedwars.player.BedwarsPlayer;
import fr.trytoon.bedwars.player.PlayerManager;
import fr.trytoon.bedwars.scoreboard.BedwarsScoreboardManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Bed;
import org.bukkit.scoreboard.ScoreboardManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Bukkit.getServer;

public class TeamManager implements Listener {

    BedwarsPlugin plugin;

    Map<String, BedwarsTeam> teams = new HashMap<>();


    public TeamManager(BedwarsPlugin plugin) {
        this.plugin = plugin;
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

    public BedwarsTeam getTeam(String key) {
        return teams.get(key);
    }

    public Map<String, BedwarsTeam> getTeams() {
        return this.teams;
    }

    public BedwarsTeam getPlayerTeam(Player player) {
        BedwarsPlayer bedwarsPlayer = plugin.getPlayerManager().getBedwarsPlayer(player);

        if (bedwarsPlayer != null) {
            return bedwarsPlayer.getTeam();
        }
        else {
            for (BedwarsTeam bedwarsTeam : teams.values()) {
                if (bedwarsTeam.getPlayers().contains(player)) {
                    return bedwarsTeam;
                }
            }
        }

        return null;
    }

    public void addPlayer(String teamName, Player player) {
        BedwarsTeam bedwarsTeam = teams.get(teamName);
        if (bedwarsTeam != null) {

            if (getPlayerTeam(player) != null) {
                removePlayer(getPlayerTeam(player).getName(), player);
            }

            if (bedwarsTeam.getPlayersCount() < bedwarsTeam.getMaxMembers()) {
                bedwarsTeam.addToTeam(player);

                PlayerTeamJoinEvent teamJoinEvent = new PlayerTeamJoinEvent(player, bedwarsTeam);
                Bukkit.getServer().getPluginManager().callEvent(teamJoinEvent);

                plugin.getPlayerManager().createBedwarsPlayer(player);

                BedwarsPlayer bedwarsPlayer = plugin.getPlayerManager().getBedwarsPlayer(player);
                if (bedwarsPlayer != null) {
                    bedwarsPlayer.setTeam(bedwarsTeam);
                }


                player.sendMessage("Vous avez officiellement rejoint la team: " + teamName);
            }
            else {
                player.sendMessage("Erreur ! L'équipe " + teamName + " est complète.");
            }
        }
    }

    public void removePlayer(String teamName, Player player) {
        BedwarsTeam bedwarsTeam = teams.get(teamName);
        if (bedwarsTeam != null) {
            if (bedwarsTeam.hasPlayer(player)) {
                bedwarsTeam.removeFromTeam(player);

            }
        }
    }

    @EventHandler
    public void onTeamJoin(PlayerTeamSelectEvent event) {
        String teamName = event.getTeamName();
        Player player = event.getPlayer();

        addPlayer(teamName, player);
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


    //todo get blockstate for bed to break both parts
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
        if (block.getType() == Material.BED || block.getType() == Material.BED_BLOCK) {
            Bed bed = (Bed) block.getState().getData();
            BlockFace facing = bed.getFacing();


            Location otherPartLocation = bedHeadLocation.clone();

            boolean isHead = bed.isHeadOfBed();

            switch (facing) {
                case NORTH:
                    if (isHead) {
                        otherPartLocation.add(0, 0, 1);
                    } else {
                        otherPartLocation.add(0, 0, -1);
                    }
                    break;
                case SOUTH:
                    if (isHead) {
                        otherPartLocation.add(0, 0, -1);
                    } else {
                        otherPartLocation.add(0, 0, 1);
                    }
                    break;
                case EAST:
                    if (isHead) {
                        otherPartLocation.add(-1, 0, 0);
                    } else {
                        otherPartLocation.add(1, 0, 0);
                    }
                    break;
                case WEST:
                    if (isHead) {
                        otherPartLocation.add(1, 0, 0);
                    } else {
                        otherPartLocation.add(-1, 0, 0);
                    }
                    break;
                default:
                    return bedHeadLocation;
            }
            return otherPartLocation;
        }

        return bedHeadLocation;
    }




    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (plugin.getGameManager() != null && plugin.getGameManager().getCurrentGameState() == GameState.WAITING_FOR_PLAYER) {
            Player player = event.getPlayer();

            BedwarsTeam playerTeam = getPlayerTeam(player);
            if (playerTeam != null) {
                removePlayer(playerTeam.getName(), player);

                PlayerTeamJoinEvent teamJoinEvent = new PlayerTeamJoinEvent(player, null);
                Bukkit.getServer().getPluginManager().callEvent(teamJoinEvent);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBedBroken(BedBrokenEvent event) {
        BedwarsTeam team = event.getTeam();

        team.setBedBroken(true);
        ChatColor color = team.getTeamColor().getChatColor();
        Bukkit.broadcastMessage("The bed of team "
                + color + team.getName()
                + ChatColor.WHITE
                + " was broken by "
                + event.getBreaker().getName());

        for (Player player : getTeam(team.getName()).getPlayers()) {
            player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 1.0f, 1.0f);
            TitleAPI.sendTitle(player, 10,20,30, "Your Bed was broken.", "");
        }
    }
}
