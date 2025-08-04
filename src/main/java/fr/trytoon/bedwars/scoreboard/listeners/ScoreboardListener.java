package fr.trytoon.bedwars.scoreboard.listeners;

import fr.trytoon.bedwars.events.GameStatusChangeEvent;
import fr.trytoon.bedwars.events.PlayerTeamChangeEvent;
import fr.trytoon.bedwars.events.TeamBedBrokenEvent;
import fr.trytoon.bedwars.game.GameManager;
import fr.trytoon.bedwars.game.GameState;
import fr.trytoon.bedwars.scoreboard.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ScoreboardListener implements Listener {

    private final BedwarsScoreboardManager scoreboardManager;
    private final ScoreboardProvider scoreboardProvider;
    private final GameManager gameManager;


    public ScoreboardListener(BedwarsScoreboardManager scoreboardManager, ScoreboardProvider scoreboardProvider, GameManager gameManager) {
        this.scoreboardManager = scoreboardManager;
        this.scoreboardProvider = scoreboardProvider;
        this.gameManager = gameManager;
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        GameState currentGameState = gameManager.getCurrentGameState();
        AbstractBedwarsScoreboard scoreboard = null;

        if (currentGameState == GameState.WAITING_FOR_PLAYER) {
            scoreboard = scoreboardProvider.createTeamSelectionScoreboard(player);
        } else if (currentGameState == GameState.PLAYING) {
            scoreboard = scoreboardProvider.createGameScoreboard(player);
        }

        if (scoreboard != null) {
            scoreboardManager.registerScoreboard(player, scoreboard);
            scoreboard.update();
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        scoreboardManager.unregisterScoreboard(player);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerTeamJoin(PlayerTeamChangeEvent event) {
        GameState currentGameState = gameManager.getCurrentGameState();

        if (currentGameState == GameState.WAITING_FOR_PLAYER) {
            scoreboardManager.updateScoreboardsOfType(TeamSelectionScoreboard.class);
        }
    }

    @EventHandler
    public void onGameStarted(GameStatusChangeEvent event) {
        if (event.getStateAfter() == GameState.PLAYING) {
            scoreboardManager.updateToGameScoreboards(scoreboardProvider);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBedBroken(TeamBedBrokenEvent event) {
        GameState currentGameState = gameManager.getCurrentGameState();

        if (currentGameState == GameState.PLAYING) {
            scoreboardManager.updateScoreboardsOfType(GameScoreboard.class);
        }
    }
}
