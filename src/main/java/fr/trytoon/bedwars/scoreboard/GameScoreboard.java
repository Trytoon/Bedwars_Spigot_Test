package fr.trytoon.bedwars.scoreboard;

import fr.trytoon.bedwars.player.BedwarsPlayer;
import fr.trytoon.bedwars.player.PlayerManager;
import fr.trytoon.bedwars.teams.BedwarsTeam;
import fr.trytoon.bedwars.teams.TeamManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameScoreboard extends AbstractBedwarsScoreboard {

    private final PlayerManager playerManager;
    private final TeamManager teamManager;

    public GameScoreboard(Player player, PlayerManager playerManager, TeamManager teamManager) {
        super(player);
        this.playerManager = playerManager;
        this.teamManager = teamManager;

        update();
    }

    @Override
    protected List<String> getLines() {
        BedwarsPlayer bedwarsPlayer = playerManager.getBedwarsPlayer(player);

        List<String> content = new ArrayList<>();
        content.add("");

        if (teamManager != null) {
            for (BedwarsTeam team : teamManager.getTeams().values()) {

                ChatColor color = team.getTeamColor().getChatColor();

                boolean isBedBroken = team.isBedBroken();

                String aliveString;
                if (!isBedBroken) {
                    aliveString = ChatColor.GREEN + " ✓";
                }
                else {
                    int aliveCount = team.getPlayersCount() - team.getDeadMembers();
                    aliveString = (aliveCount == 0)
                            ? ChatColor.RED + " ✗"
                            : ChatColor.WHITE + " " + aliveCount + "/" + team.getPlayersCount();
                }

                String youString = "";
                if (bedwarsPlayer.getTeam().equals(team)) {
                    youString += ChatColor.GRAY + "YOU";
                }

                String teamLine = String.format("%s%s%s %s", color, team.getName(), aliveString, youString);

                content.add(teamLine);
            }
        }

        content.add("");
        content.add(ChatColor.WHITE + "Kills: " + bedwarsPlayer.getKills());
        content.add(ChatColor.WHITE + "Bed Destroyed: " + bedwarsPlayer.getBedDestroyed());
        content.add("");
        content.add(ChatColor.YELLOW + "play.test.fr");

        return content;
    }
}
