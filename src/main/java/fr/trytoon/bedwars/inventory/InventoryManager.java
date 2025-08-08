package fr.trytoon.bedwars.inventory;

import fr.trytoon.bedwars.BedwarsPlugin;
import fr.trytoon.bedwars.inventory.base.AbstractBedwarsInventory;
import fr.trytoon.bedwars.player.BedwarsPlayer;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class InventoryManager implements Listener {

    private final BedwarsPlugin plugin;
    private final Map<BedwarsPlayer, AbstractBedwarsInventory> openedInventories;

    public InventoryManager(BedwarsPlugin plugin) {
        this.plugin = plugin;
        this.openedInventories = new HashMap<>();
    }

    public AbstractBedwarsInventory getInventoryForPlayer(BedwarsPlayer player) {
        return openedInventories.get(player);
    }

    public void registerInventoryForPlayer(BedwarsPlayer player, AbstractBedwarsInventory inventory) {
        if (player == null || inventory == null) return;

        unregisterInventoryForPlayer(player);
        openedInventories.put(player, inventory);
    }

    public void unregisterInventoryForPlayer(BedwarsPlayer player) {
        if (player == null) return;
        openedInventories.remove(player);
    }

    public Map<BedwarsPlayer, AbstractBedwarsInventory> getOpenedInventories() {
        return openedInventories;
    }
}
