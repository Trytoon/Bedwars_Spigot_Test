package fr.trytoon.bedwars.world.listeners;

import fr.trytoon.bedwars.events.TeamBedBrokenEvent;
import fr.trytoon.bedwars.game.GameManager;
import fr.trytoon.bedwars.game.GameState;
import fr.trytoon.bedwars.player.BedwarsPlayer;
import fr.trytoon.bedwars.player.PlayerManager;
import fr.trytoon.bedwars.teams.BedwarsTeam;
import fr.trytoon.bedwars.teams.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Optional;

public class WorldEventsListener implements Listener {

    private final GameManager gameManager;
    private final TeamManager teamManager;
    private final PlayerManager playerManager;

    public WorldEventsListener(GameManager gameManager, TeamManager teamManager, PlayerManager playerManager) {
        this.gameManager = gameManager;
        this.teamManager = teamManager;
        this.playerManager = playerManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (gameManager.getCurrentGameState() == GameState.PLAYING) {

            Block block = event.getBlock();
            Player player = event.getPlayer();

            BedwarsPlayer bedwarsPlayer = playerManager.getBedwarsPlayer(player);

            if (bedwarsPlayer == null) return;

            BedwarsTeam teamByBed = teamManager.findTeamByBed(block.getLocation());

            if (teamByBed != null) {
                event.getBlock().getDrops().clear();

                Optional.ofNullable(bedwarsPlayer.getTeam()).ifPresent(playerTeam -> {
                    if (playerTeam.equals(teamByBed)) {
                        event.setCancelled(true);
                        player.sendMessage(ChatColor.RED + "You cannot break your own bed !");
                    } else {
                        playerTeam.setBedBroken(false);
                        TeamBedBrokenEvent teamBedBrokenEvent = new TeamBedBrokenEvent(teamByBed, player);
                        Bukkit.getServer().getPluginManager().callEvent(teamBedBrokenEvent);
                    }
                });
            } else {
                if (!gameManager.isBlockPlaced(block)) {
                    event.getBlock().getDrops().clear();
                    event.setCancelled(true);

                    player.sendMessage(ChatColor.RED + "You cannot break this block !");
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        gameManager.addPlacedBlockByPlayer(block);
    }
}
