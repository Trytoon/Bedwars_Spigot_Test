package fr.trytoon.bedwars.player;

import com.connorlinfoot.titleapi.TitleAPI;
import fr.trytoon.bedwars.BedwarsPlugin;
import fr.trytoon.bedwars.game.GameManager;
import fr.trytoon.bedwars.game.GameState;
import fr.trytoon.bedwars.teams.BedwarsTeam;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerRespawnManager {

    private final BedwarsPlugin plugin;

    public PlayerRespawnManager(BedwarsPlugin plugin) {
        this.plugin = plugin;
    }

    public void respawnPlayer(Player player) {
        GameManager gameManager = plugin.getGameManager();
        PlayerManager playerManager = plugin.getPlayerManager();

        if (player == null || gameManager.getCurrentGameState() != GameState.PLAYING) return;

        BedwarsPlayer bedwarsPlayer = playerManager.getBedwarsPlayer(player);
        BedwarsTeam team = bedwarsPlayer.getTeam();

        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(player.getWorld().getSpawnLocation());

        if (team == null) return;

        if (!team.isBedBroken()) {
            new BukkitRunnable() {
                private int timeLeft = 10;

                @Override
                public void run() {
                    if (!player.isOnline()) {
                        cancel();
                        return;
                    }

                    if (timeLeft > 0) {
                        TitleAPI.sendTitle(player, 0, 21, 0, ChatColor.YELLOW + "Respawn in : " + timeLeft + " s", "");
                        timeLeft--;
                        return;
                    }


                    cancel();
                    player.setGameMode(GameMode.SURVIVAL);
                    player.setHealth(player.getMaxHealth());
                    player.teleport(team.getSpawn());
                }
            }.runTaskTimer(plugin, 0L, 20L);
        } else {
            TitleAPI.sendTitle(player, 0, 40, 0, ChatColor.RED + "You are eliminated !", "");
            team.incrementDeadPlayers();
        }
    }
}
