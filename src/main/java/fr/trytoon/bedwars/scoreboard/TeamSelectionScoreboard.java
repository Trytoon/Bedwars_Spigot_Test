package fr.trytoon.bedwars.scoreboard;

import fr.trytoon.bedwars.teams.BedwarsTeam;
import fr.trytoon.bedwars.teams.TeamManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TeamSelectionScoreboard extends AbstractBedwarsScoreboard{

    private final TeamManager teamManager;

    public TeamSelectionScoreboard(Player player, TeamManager teamManager) {
        super(player);
        this.teamManager = teamManager;

        update();
    }

    @Override
    protected List<String> getLines() {
        List<String> content = new ArrayList<>();
        content.add("");

        if (teamManager != null) {
            for (BedwarsTeam team : teamManager.getTeams().values()) {

                ChatColor color = team.getTeamColor().getChatColor();

                String teamLine = String.format("%s%s%s: %d/%d",
                        color, team.getName(), ChatColor.WHITE,
                        team.getPlayersCount(), team.getMaxMembers());
                content.add(teamLine);
            }
        }

        content.add("");
        content.add(ChatColor.YELLOW + "play.test.fr");

        return content;
    }
}
