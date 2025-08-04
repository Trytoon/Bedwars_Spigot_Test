package fr.trytoon.bedwars.teams.listeners;

import com.connorlinfoot.titleapi.TitleAPI;
import fr.trytoon.bedwars.events.PlayerTeamChangeEvent;
import fr.trytoon.bedwars.events.TeamBedBrokenEvent;
import fr.trytoon.bedwars.player.BedwarsPlayer;
import fr.trytoon.bedwars.teams.BedwarsTeam;
import fr.trytoon.bedwars.teams.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class TeamListener implements Listener {

    private final TeamManager teamManager;

    public TeamListener(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @EventHandler
    public void onTeamJoin(PlayerTeamChangeEvent event) {
        BedwarsPlayer bedwarsPlayer = event.getBedwarsPlayer();

        if (bedwarsPlayer == null) return;

        if (event.getTeamBefore() != null) {
            teamManager.removePlayerFromTeam(event.getTeamBefore(), bedwarsPlayer);
        }

        teamManager.addPlayerToTeam(event.getTeamAfter(), bedwarsPlayer);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onTeamBedBroken(TeamBedBrokenEvent event) {
        BedwarsTeam team = event.getTeam();

        team.setBedBroken(true);
        ChatColor color = team.getTeamColor().getChatColor();
        Bukkit.broadcastMessage("The bed of team "
                + color + team.getName()
                + ChatColor.WHITE
                + " was broken by "
                + event.getBreaker().getName());

        for (BedwarsPlayer bedwarsPlayer : teamManager.getTeamByName(team.getName()).getPlayers()) {
            Player player = bedwarsPlayer.getPlayer();

            player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 1.0f, 1.0f);
            TitleAPI.sendTitle(player, 10,20,30, "Your Bed was broken.", "");
        }
    }
}
