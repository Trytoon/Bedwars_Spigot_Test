package fr.trytoon.bedwars;

import de.tr7zw.changeme.nbtapi.NBT;
import fr.trytoon.bedwars.commands.CommandManager;
import fr.trytoon.bedwars.game.GameManager;
import fr.trytoon.bedwars.inventory.InventoryManager;
import fr.trytoon.bedwars.inventory.listeners.InventoryListener;
import fr.trytoon.bedwars.items.GeneratorManager;
import fr.trytoon.bedwars.player.PlayerManager;
import fr.trytoon.bedwars.player.PlayerRespawnManager;
import fr.trytoon.bedwars.player.listeners.PlayerListener;
import fr.trytoon.bedwars.scoreboard.BedwarsScoreboardManager;
import fr.trytoon.bedwars.scoreboard.ScoreboardProvider;
import fr.trytoon.bedwars.scoreboard.listeners.ScoreboardListener;
import fr.trytoon.bedwars.teams.TeamManager;
import fr.trytoon.bedwars.teams.listeners.TeamListener;
import fr.trytoon.bedwars.world.listeners.WorldEventsListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class BedwarsPlugin extends JavaPlugin {

    private  GameManager gameManager;
    private PlayerManager playerManager;
    private PlayerRespawnManager respawnManager;

    private TeamManager teamManager;
    private CommandManager commandManager;
    private BedwarsScoreboardManager scoreboardManager;
    private GeneratorManager generatorManager;
    private InventoryManager inventoryManager;

    private ScoreboardProvider scoreboardProvider;

    @Override
    public void onEnable() {
        if (!NBT.preloadApi()) {
            getLogger().warning("NBT-API wasn't initialized properly, disabling the plugin");
            getPluginLoader().disablePlugin(this);
            return;
        }

        getLogger().info("[BEDWARS] Plugin enable");

        gameManager = new GameManager(this);
        playerManager = new PlayerManager(this);
        respawnManager = new PlayerRespawnManager(this);
        teamManager = new TeamManager(this);
        scoreboardManager = new BedwarsScoreboardManager(this);
        generatorManager = new GeneratorManager(this);
        inventoryManager = new InventoryManager(this);
        commandManager = new CommandManager(this);

        scoreboardProvider = new ScoreboardProvider(teamManager, playerManager);

        registerListeners();
    }

    @Override
    public void onDisable() {
        getLogger().info("[BEDWARS] Plugin disabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return commandManager.onCommand(sender, cmd, label, args);
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(playerManager, gameManager, respawnManager, teamManager), this);
        getServer().getPluginManager().registerEvents(new WorldEventsListener(gameManager, teamManager, playerManager), this);
        getServer().getPluginManager().registerEvents(new TeamListener(teamManager), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(teamManager, playerManager, inventoryManager), this);
        getServer().getPluginManager().registerEvents(new ScoreboardListener(scoreboardManager, scoreboardProvider, gameManager), this);
    }

    public File getConfigurationFileFromPath(String path) {
        return new File(getDataFolder(), path);
    }

    public FileConfiguration getYAMLConfigurationFromPath(String filePath) {
        File file = new File(getDataFolder(), filePath);

        if (!file.exists()) {
            getLogger().warning("[BEDWARS] Création de " + filePath + " car le fichier n'existe pas.");

            try {
                if (getResource(filePath) != null) {
                    saveResource(filePath, false);
                } else {
                    file.createNewFile();
                    getLogger().info("[BEDWARS] Création d'un nouveau fichier vide : " + filePath);
                }
            } catch (IOException e) {
                getLogger().severe("[BEDWARS] Échec de la création ou de la sauvegarde du fichier : " + filePath);
                e.printStackTrace();
            }
        }

        return YamlConfiguration.loadConfiguration(file);
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
