package fr.trytoon.bedwars.player.listeners;

import fr.trytoon.bedwars.events.PlayerTeamChangeEvent;
import fr.trytoon.bedwars.game.GameManager;
import fr.trytoon.bedwars.game.GameState;
import fr.trytoon.bedwars.items.BedwarsItem;
import fr.trytoon.bedwars.player.BedwarsPlayer;
import fr.trytoon.bedwars.player.PlayerManager;
import fr.trytoon.bedwars.player.PlayerRespawnManager;
import fr.trytoon.bedwars.teams.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class PlayerListener implements Listener {

    private final PlayerManager playerManager;
    private final GameManager gameManager;
    private final PlayerRespawnManager respawnManager;
    private final TeamManager teamManager;

    public PlayerListener(PlayerManager playerManager, GameManager gameManager, PlayerRespawnManager respawnManager, TeamManager teamManager) {
        this.playerManager = playerManager;
        this.gameManager = gameManager;
        this.respawnManager = respawnManager;
        this.teamManager = teamManager;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (gameManager.getCurrentGameState() == GameState.PLAYING) {
            if (event.getEntity() instanceof Player player) {
                if (event.getDamage() >= player.getHealth()) {
                    event.setCancelled(true);

                    player.getInventory().clear();
                    player.getInventory().setArmorContents(new ItemStack[4]);

                    respawnManager.respawnPlayer(player);

                    playerManager.getBedwarsPlayer(player).incrementDeaths();
                }
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (gameManager.getCurrentGameState() == GameState.PLAYING || gameManager.getCurrentGameState() == GameState.WAITING_FOR_PLAYER) {
            if (event.getEntity() instanceof Player player) {
                player.setFoodLevel(20);
                player.setSaturation(20.0f);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (gameManager != null && gameManager.getCurrentGameState() == GameState.WAITING_FOR_PLAYER) {
            Player player = event.getPlayer();

            player.getInventory().clear();
            player.getInventory().setItem(8, BedwarsItem.TEAM_SELECTOR.toItemStack());
            player.getInventory().setItem(0, BedwarsItem.SHOP_TEST.toItemStack());

            Location spawnLocation = player.getWorld().getSpawnLocation();
            player.teleport(spawnLocation);

            player.setGameMode(GameMode.ADVENTURE);

            playerManager.createBedwarsPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (gameManager != null && gameManager.getCurrentGameState() == GameState.WAITING_FOR_PLAYER) {
            Player player = event.getPlayer();

            BedwarsPlayer bedwarsPlayer = playerManager.removeBedwarsPlayer(player); // récupère et supprime en un seul appel

            if (bedwarsPlayer != null) {
                Optional.ofNullable(bedwarsPlayer.getTeam()).ifPresent(team -> {
                    teamManager.removePlayerFromTeam(team, bedwarsPlayer);

                    PlayerTeamChangeEvent teamChangeEvent = new PlayerTeamChangeEvent(bedwarsPlayer, team, null);
                    Bukkit.getServer().getPluginManager().callEvent(teamChangeEvent);
                });
            }
        }
    }
}

