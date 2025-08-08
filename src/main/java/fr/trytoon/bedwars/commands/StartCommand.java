package fr.trytoon.bedwars.commands;

import fr.trytoon.bedwars.events.GameStatusChangeEvent;
import fr.trytoon.bedwars.game.GameManager;
import fr.trytoon.bedwars.game.GameState;
import fr.trytoon.bedwars.items.GeneratorManager;
import fr.trytoon.bedwars.player.BedwarsPlayer;
import fr.trytoon.bedwars.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartCommand implements CommandExecutor {

    private final GameManager gameManager;
    private final PlayerManager playerManager;
    private final GeneratorManager generatorManager;

    public StartCommand(GameManager gameManager, PlayerManager playerManager, GeneratorManager generatorManager) {
        this.gameManager = gameManager;
        this.playerManager = playerManager;
        this.generatorManager = generatorManager;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        GameState currentGameState = gameManager.getCurrentGameState();
        GameState nextGameState =GameState.PLAYING;

        gameManager.setCurrentGameState(nextGameState);

        for (Player player : Bukkit.getOnlinePlayers()) {
            BedwarsPlayer bedwarsPlayer =  playerManager.getBedwarsPlayer(player);

            if (bedwarsPlayer != null) {
                if (bedwarsPlayer.getTeam() != null) {
                    player.getInventory().clear();
                    player.teleport(bedwarsPlayer.getTeam().getSpawn());
                    player.setGameMode(GameMode.SURVIVAL);
                }
            }
        }

        generatorManager.run();

        GameStatusChangeEvent event = new GameStatusChangeEvent(currentGameState, nextGameState);
        Bukkit.getServer().getPluginManager().callEvent(event);

        return true;
    }
}
