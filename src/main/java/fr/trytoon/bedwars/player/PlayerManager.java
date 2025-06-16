package fr.trytoon.bedwars.player;

import com.connorlinfoot.titleapi.TitleAPI;
import fr.trytoon.bedwars.BedwarsPlugin;
import fr.trytoon.bedwars.events.BedBrokenEvent;
import fr.trytoon.bedwars.game.GameManager;
import fr.trytoon.bedwars.game.GameState;
import fr.trytoon.bedwars.teams.BedwarsTeam;
import fr.trytoon.bedwars.teams.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager implements Listener {

    final BedwarsPlugin plugin;
    Map<UUID, BedwarsPlayer> players;

    public PlayerManager(BedwarsPlugin plugin) {
        this.plugin = plugin;
        players = new HashMap<>();
    }

    public BedwarsPlayer createBedwarsPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        BedwarsPlayer bwPlayer = new BedwarsPlayer(player);
        players.put(uuid, bwPlayer);

        return bwPlayer;
    }

    public BedwarsPlayer removeBedwarsPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        BedwarsPlayer bedwarsPlayer = players.get(uuid);
        players.remove(uuid);

        return bedwarsPlayer;
    }

    public BedwarsPlayer getBedwarsPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        return players.get(uuid);
    }

    public Collection<BedwarsPlayer> getBedwarsPlayers() {
        return players.values();
    }

    public void respawnPlayer(Player player) {
        PlayerManager playerManager = plugin.getPlayerManager();
        GameManager gameManager = plugin.getGameManager();
        if (player != null && gameManager.getCurrentGameState() == GameState.PLAYING) {
            BedwarsPlayer bedwarsPlayer = playerManager.getBedwarsPlayer(player);

            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(player.getWorld().getSpawnLocation());

            BedwarsTeam bedwarsTeam = getBedwarsPlayer(player).getTeam();

            if (!bedwarsPlayer.getTeam().isBedBroken()) {

                new BukkitRunnable() {
                    int timeLeft = 10;
                    @Override
                    public void run() {
                        if (timeLeft >= 1) {
                            TitleAPI.sendTitle(player, 0, 21, 0, ChatColor.YELLOW + "Respawn in : " + timeLeft + " s", "");
                        }

                        if (timeLeft <= 0) {
                            this.cancel();

                            player.setGameMode(GameMode.SURVIVAL);
                            player.setHealth(player.getMaxHealth());


                            if (bedwarsTeam != null) {
                                player.teleport(bedwarsTeam.getSpawn());
                            } else {
                                player.teleport(player.getWorld().getSpawnLocation());
                            }
                        } else {
                            timeLeft--;
                        }
                    }
                }.runTaskTimer(plugin, 0L, 20L);
            } else {
                TitleAPI.sendTitle(player, 0, 40, 0, ChatColor.RED + "You are eliminated !", "");
                bedwarsTeam.setDeadMembers(bedwarsTeam.getDeadMembers() + 1);
            }
        }
    }

    public void killPlayerByPlayer(Player killed, Player killer) {


    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (plugin.getGameManager() != null && plugin.getGameManager().getCurrentGameState() == GameState.WAITING_FOR_PLAYER) {
            Player player = event.getPlayer();
            createBedwarsPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (plugin.getGameManager() != null && plugin.getGameManager().getCurrentGameState() == GameState.WAITING_FOR_PLAYER) {
            Player player = event.getPlayer();
            removeBedwarsPlayer(player);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        GameManager gameManager = plugin.getGameManager();

        if (gameManager.getCurrentGameState() == GameState.PLAYING) {
            TeamManager teamManager = plugin.getTeamManager();
            Block block = event.getBlock();
            Player player = event.getPlayer();

            BedwarsTeam team = teamManager.findTeamByBed(block.getLocation());

            if (team != null) {
                event.getBlock().getDrops().clear();


                BedwarsTeam playerTeam = teamManager.getPlayerTeam(player);
                if (playerTeam.equals(team)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You cannot break your own bed !");
                }
                else {
                    playerTeam.setBedBroken(false);
                    BedBrokenEvent bedBrokenEvent = new BedBrokenEvent(team, player);
                    Bukkit.getServer().getPluginManager().callEvent(bedBrokenEvent);
                }

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
        GameManager gameManager = plugin.getGameManager();
        Block block = event.getBlock();
        gameManager.addPlacedBlock(block);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getDamage() >= player.getHealth()) {
                event.setCancelled(true);
                player.getInventory().clear();
                ItemStack[] emptyArmor = new ItemStack[4];
                player.getInventory().setArmorContents(emptyArmor);

                respawnPlayer(player);

                getBedwarsPlayer(player).setDeaths(getBedwarsPlayer(player).getDeaths() + 1);

                if (event instanceof EntityDamageByEntityEvent entityDamageEvent) {
                    if (entityDamageEvent.getDamager() instanceof Player killer) {
                        killPlayerByPlayer(player, killer);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);

        if (event.getEntity() instanceof Player player) {
            player.setFoodLevel(20);
            player.setSaturation(20.0f);
        }
    }
}
