package fr.trytoon.bedwars.scoreboard;

import fr.mrmicky.fastboard.FastBoard;
import fr.trytoon.bedwars.BedwarsPlugin;
import fr.trytoon.bedwars.events.BedBrokenEvent;
import fr.trytoon.bedwars.events.GameStartedEvent;
import fr.trytoon.bedwars.events.PlayerTeamJoinEvent;
import fr.trytoon.bedwars.game.GameState;
import fr.trytoon.bedwars.player.BedwarsPlayer;
import fr.trytoon.bedwars.player.PlayerManager;
import fr.trytoon.bedwars.teams.BedwarsTeam;
import fr.trytoon.bedwars.teams.TeamManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class BedwarsScoreboardManager implements Listener {

    private
    BedwarsPlugin plugin;
    private
    Map<UUID, FastBoard> boards = new HashMap<>();

    public BedwarsScoreboardManager(BedwarsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        FastBoard board = new FastBoard(player);

        board.updateTitle(ChatColor.YELLOW + "BEDWARS");

        if (plugin.getGameManager().getCurrentGameState() == GameState.WAITING_FOR_PLAYER) {
            board.updateLines(getTeamScoreboardContent());
        } else {
            board.updateLines(getGameScoreboardContentForPlayer(player));
        }

        boards.put(player.getUniqueId(), board);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        FastBoard board = boards.remove(player.getUniqueId());

        if (board != null) {
            board.delete();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerTeamJoin(PlayerTeamJoinEvent event) {
        if (plugin.getGameManager().getCurrentGameState() == GameState.WAITING_FOR_PLAYER) {
            updateAllScoreboards(getTeamScoreboardContent());
        }
    }

    @EventHandler
    public void onGameStarted(GameStartedEvent event) {
        updateAllGameScoreboards();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBedBroken(BedBrokenEvent event) {
        updateAllGameScoreboards();
    }

    private void updateAllScoreboards(List<String> content) {
        for (FastBoard board : boards.values()) {
            board.updateLines(content);
        }
    }

    private List<String> getTeamScoreboardContent() {

        TeamManager teamManager = plugin.getTeamManager();

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

    private List<String> getGameScoreboardContentForPlayer(Player player) {
        PlayerManager playerManager = plugin.getPlayerManager();

        BedwarsPlayer bedwarsPlayer = playerManager.getBedwarsPlayer(player);

        TeamManager teamManager = plugin.getTeamManager();

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

    public void updateAllGameScoreboards() {
        for (UUID uuid : boards.keySet()) {

            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null && player.isOnline()) {
                boards.get(uuid).updateLines(getGameScoreboardContentForPlayer(player));
            }
        }
    }


    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getDamage() >= player.getHealth()) {
                PlayerManager playerManager = plugin.getPlayerManager();
                if (playerManager != null) {
                    BedwarsPlayer bedwarsPlayer = playerManager.getBedwarsPlayer(player);
                    if (bedwarsPlayer.getTeam() != null && bedwarsPlayer.getTeam().isBedBroken()) {
                        updateAllGameScoreboards();
                    }
                }
            }
        }
    }
}
