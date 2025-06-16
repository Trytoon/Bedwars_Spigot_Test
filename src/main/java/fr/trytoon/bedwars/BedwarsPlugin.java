package fr.trytoon.bedwars;

import de.tr7zw.changeme.nbtapi.NBT;

import fr.trytoon.bedwars.commands.CommandManager;
import fr.trytoon.bedwars.game.GameManager;
import fr.trytoon.bedwars.inventory.TeamSelectorInventory;
import fr.trytoon.bedwars.items.GeneratorManager;
import fr.trytoon.bedwars.player.PlayerManager;
import fr.trytoon.bedwars.scoreboard.BedwarsScoreboardManager;
import fr.trytoon.bedwars.teams.TeamManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class BedwarsPlugin extends JavaPlugin {
    GameManager gameManager;

    PlayerManager playerManager;

    TeamManager teamManager;
    CommandManager commandManager;

    BedwarsScoreboardManager scoreboardManager;

    GeneratorManager generatorManager;

    @Override
    public void onEnable() {
        if (!NBT.preloadApi()) {
            getLogger().warning("NBT-API wasn't initialized properly, disabling the plugin");
            getPluginLoader().disablePlugin(this);
            return;
        }

        getLogger().info("[BEDWARS] plugin enable");

        gameManager = new GameManager(this);

        playerManager = new PlayerManager(this);

        teamManager = new TeamManager(this);
        teamManager.loadTeams();

        commandManager = new CommandManager(this);
        commandManager.registerCommands();

        scoreboardManager = new BedwarsScoreboardManager(this);

        getServer().getPluginManager().registerEvents(playerManager, this);
        getServer().getPluginManager().registerEvents(teamManager, this);
        getServer().getPluginManager().registerEvents(new TeamSelectorInventory(teamManager), this);

        getServer().getPluginManager().registerEvents(scoreboardManager, this);

        generatorManager = new GeneratorManager(this);

    }

    @Override
    public void onDisable() {
        getLogger().info("[BEDWARS] plugin disable");
    }

    public File getConfigurationFileFromPath(String path) {
        return new File(getDataFolder(), path);
    }

    public FileConfiguration getYAMLConfigurationFromPath(String filePath) {
        File file = new File(getDataFolder(), filePath);

        if (!file.exists()) {
            getLogger().warning("[BEDWARS] Creating " + filePath + " as it does not exist.");

            try {
                if (getResource(filePath) != null) {
                    saveResource(filePath, false);
                } else {
                    file.createNewFile();
                    getLogger().info("[BEDWARS] Created a new empty file: " + filePath);
                }
            } catch (IOException e) {
                getLogger().severe("[BEDWARS] Failed to create or save the file: " + filePath);
                e.printStackTrace();
            }
        }

        return YamlConfiguration.loadConfiguration(file);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return commandManager.onCommand(sender, cmd, label, args);
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public BedwarsScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public GeneratorManager getGeneratorManager() {
        return generatorManager;
    }

}
