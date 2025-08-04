package fr.trytoon.bedwars.scoreboard;

import fr.trytoon.bedwars.player.PlayerManager;
import fr.trytoon.bedwars.teams.TeamManager;
import org.bukkit.entity.Player;

public class ScoreboardProvider {

    private final TeamManager teamManager;
    private final PlayerManager playerManager;

    public ScoreboardProvider(TeamManager teamManager, PlayerManager playerManager) {
        this.teamManager = teamManager;
        this.playerManager = playerManager;
    }

    public TeamSelectionScoreboard createTeamSelectionScoreboard(Player player) {
        return new TeamSelectionScoreboard(player, teamManager);
    }

    public GameScoreboard createGameScoreboard(Player player) {
        return new GameScoreboard(player, playerManager, teamManager);
    }
}
