package fr.trytoon.bedwars.commands;

import fr.trytoon.bedwars.BedwarsConstants;
import fr.trytoon.bedwars.BedwarsPlugin;
import fr.trytoon.bedwars.events.GameStartedEvent;
import fr.trytoon.bedwars.game.GameManager;
import fr.trytoon.bedwars.game.GameState;
import fr.trytoon.bedwars.teams.BedwarsTeam;
import fr.trytoon.bedwars.teams.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartCommand implements CommandExecutor {

    BedwarsPlugin plugin;

    public StartCommand(BedwarsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        TeamManager teamManager = plugin.getTeamManager();
        GameManager gameManager = plugin.getGameManager();

        World world = plugin.getServer().getWorld(BedwarsConstants.TEST_WORLD);
        if (world == null) {
            return false;
        }

        gameManager.setCurrentGameState(GameState.PLAYING);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(world)) {
                BedwarsTeam playerBedwarsTeam = teamManager.getPlayerTeam(player);
                if (playerBedwarsTeam != null) {
                    player.teleport(playerBedwarsTeam.getSpawn());
                }
            }
        }

        plugin.getGeneratorManager().run();

        GameStartedEvent gameStartedEvent = new GameStartedEvent();
        Bukkit.getServer().getPluginManager().callEvent(gameStartedEvent);

        return true;
    }
}
