package fr.trytoon.bedwars.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public interface BedwarsInventory extends Listener {

    /**
     * Creates the inventory layout for this custom inventory.
     */
    Inventory createInventory();

    /**
     * Get the inventory object from custom inventory
     */
    Inventory getInventory();


    /**
     * Handles a player clicking in the custom inventory.
     */
    void handleInventoryClick(InventoryClickEvent event);

    /**
     * Handles a player interaction, if applicable to this inventory.
     */
    void handlePlayerInteract(PlayerInteractEvent event);


    /**
     * Opens the inventory for the specified player
     */
    default void openInventory(Player player) {
        if (getInventory() != null) {
            player.openInventory(getInventory());
        }
    }
}

