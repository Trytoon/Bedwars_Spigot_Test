package fr.trytoon.bedwars.scoreboard;

import fr.trytoon.bedwars.BedwarsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BedwarsScoreboardManager {

    private final BedwarsPlugin plugin;

    private Map<UUID, AbstractBedwarsScoreboard> boards = new HashMap<>();

    public BedwarsScoreboardManager(BedwarsPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerScoreboard(Player player, AbstractBedwarsScoreboard scoreboard) {
        UUID uuid = player.getUniqueId();
        boards.put(uuid, scoreboard);
    }

    public void unregisterScoreboard(Player player) {
        UUID uuid = player.getUniqueId();
        boards.remove(uuid);
    }

    public void updateScoreboardsOfType(Class<? extends AbstractBedwarsScoreboard> type) {
        for (AbstractBedwarsScoreboard scoreboard : boards.values()) {
            if (type.isInstance(scoreboard)) {
                scoreboard.update();
            }
        }
    }

    public void updateToGameScoreboards(ScoreboardProvider scoreboardProvider) {
        for (UUID uuid : boards.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                GameScoreboard newGameScoreboard = scoreboardProvider.createGameScoreboard(player);
                registerScoreboard(player, newGameScoreboard);
            }
        }
    }
}
