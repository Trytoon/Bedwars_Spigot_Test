package fr.trytoon.bedwars.inventory.listeners;

import fr.trytoon.bedwars.events.PlayerTeamChangeEvent;
import fr.trytoon.bedwars.inventory.AbstractBedwarsInventory;
import fr.trytoon.bedwars.inventory.InventoryManager;
import fr.trytoon.bedwars.inventory.TeamSelectorInventory;
import fr.trytoon.bedwars.items.BedwarsItem;
import fr.trytoon.bedwars.player.BedwarsPlayer;
import fr.trytoon.bedwars.player.PlayerManager;
import fr.trytoon.bedwars.teams.TeamManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {

    private final TeamManager teamManager;
    private final PlayerManager playerManager;
    private final InventoryManager inventoryManager;

    public InventoryListener(TeamManager teamManager, PlayerManager playerManager, InventoryManager inventoryManager) {
        this.teamManager = teamManager;
        this.playerManager = playerManager;
        this.inventoryManager = inventoryManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        BedwarsPlayer bedwarsPlayer = playerManager.getBedwarsPlayer(player);
        int slot = event.getSlot();
        ItemStack item = event.getCurrentItem();
        Inventory inventory = event.getInventory();

        if (bedwarsPlayer != null) {
            AbstractBedwarsInventory bedwarsInventory = inventoryManager.getInventoryForPlayer(bedwarsPlayer);
            if (bedwarsInventory != null) {

                //on cancel l'event pour les inventaires customs
                event.setCancelled(true);
                bedwarsInventory.handleClick(bedwarsPlayer, slot, item, inventory);
            }
        }
    }

    @EventHandler
    public void onInventoryClosed(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        BedwarsPlayer bedwarsPlayer = playerManager.getBedwarsPlayer(player);

        inventoryManager.unregisterInventoryForPlayer(bedwarsPlayer);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        ItemStack item = event.getItem();

        if (item != null && item.isSimilar(BedwarsItem.TEAM_SELECTOR.toItemStack())) {
            BedwarsPlayer bedwarsPlayer = playerManager.getBedwarsPlayer(player);

            if (bedwarsPlayer != null) {
                TeamSelectorInventory teamSelectorInventory = new TeamSelectorInventory(teamManager);
                inventoryManager.registerInventoryForPlayer(bedwarsPlayer, teamSelectorInventory);

                teamSelectorInventory.open(player);
            }
        }
    }


    @EventHandler
    public void handlePlayerTeamChange(PlayerTeamChangeEvent playerTeamChangeEvent) {
        for (AbstractBedwarsInventory bedwarsInventory : inventoryManager.getOpenedInventories().values()) {
            if (bedwarsInventory instanceof TeamSelectorInventory teamSelectorInventory) {
                teamSelectorInventory.refreshInventory();
            }
        }
    }
}
