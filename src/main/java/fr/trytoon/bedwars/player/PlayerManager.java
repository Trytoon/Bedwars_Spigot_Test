package fr.trytoon.bedwars.player;

import fr.trytoon.bedwars.BedwarsPlugin;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    final BedwarsPlugin plugin;
    Map<UUID, BedwarsPlayer> players;

    public PlayerManager(BedwarsPlugin plugin) {
        this.plugin = plugin;
        players = new HashMap<>();
    }

    public BedwarsPlayer createBedwarsPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        BedwarsPlayer bwPlayer = new BedwarsPlayer(player);
        players.put(uuid, bwPlayer);

        return bwPlayer;
    }

    public BedwarsPlayer removeBedwarsPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        BedwarsPlayer bedwarsPlayer = players.get(uuid);
        players.remove(uuid);

        return bedwarsPlayer;
    }

    public BedwarsPlayer getBedwarsPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        return players.get(uuid);
    }
}
