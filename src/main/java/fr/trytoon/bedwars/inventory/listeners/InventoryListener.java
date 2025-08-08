package fr.trytoon.bedwars.inventory.listeners;

import fr.trytoon.bedwars.events.PlayerTeamChangeEvent;
import fr.trytoon.bedwars.inventory.InventoryManager;
import fr.trytoon.bedwars.inventory.ShopInventory;
import fr.trytoon.bedwars.inventory.TeamSelectorInventory;
import fr.trytoon.bedwars.inventory.base.AbstractBedwarsInventory;
import fr.trytoon.bedwars.inventory.base.AbstractPaginatedBedwarsInventory;
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
        if (!(event.getWhoClicked() instanceof Player player)) return;

        BedwarsPlayer bedwarsPlayer = playerManager.getBedwarsPlayer(player);
        if (bedwarsPlayer == null) return;

        Inventory clickedInventory = event.getInventory();
        Inventory topInventory = event.getView().getTopInventory();
        AbstractBedwarsInventory bedwarsInventory = inventoryManager.getInventoryForPlayer(bedwarsPlayer);

        if (bedwarsInventory == null) return;

        if (clickedInventory != null && clickedInventory.equals(topInventory)) {
            event.setCancelled(true);

            int slot = event.getSlot();
            ItemStack item = event.getCurrentItem();

            bedwarsInventory.handleClick(bedwarsPlayer, slot, item, clickedInventory);
        }
    }


    @EventHandler
    public void onInventoryClosed(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        BedwarsPlayer bedwarsPlayer = playerManager.getBedwarsPlayer(player);

        AbstractBedwarsInventory bedwarsInventory = inventoryManager.getInventoryForPlayer(bedwarsPlayer);

        // If we are changing page and creating a new inventory, do not unregister it
        if (bedwarsInventory instanceof AbstractPaginatedBedwarsInventory paginatedBedwarsInventory) {
            if (paginatedBedwarsInventory.isChangePage()) {
                paginatedBedwarsInventory.setChangePage(false);
                return;
            }
        }

         // also clear inventory before unregister to avoid ghost items
        if (bedwarsInventory != null && bedwarsInventory.getInventory() != null) {
            bedwarsInventory.getInventory().clear();
        }

        inventoryManager.unregisterInventoryForPlayer(bedwarsPlayer);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        //temp shop test
        if (item != null && item.isSimilar(BedwarsItem.TEAM_SELECTOR.toItemStack())) {
            BedwarsPlayer bedwarsPlayer = playerManager.getBedwarsPlayer(player);

            if (bedwarsPlayer != null) {
                TeamSelectorInventory teamSelectorInventory = new TeamSelectorInventory(teamManager);
                inventoryManager.registerInventoryForPlayer(bedwarsPlayer, teamSelectorInventory);

                teamSelectorInventory.open(player);
            }
        }

        if (item != null && item.isSimilar(BedwarsItem.SHOP_TEST.toItemStack())) {
            BedwarsPlayer bedwarsPlayer = playerManager.getBedwarsPlayer(player);

            if (bedwarsPlayer != null) {
                ShopInventory shopInventory = new ShopInventory();
                inventoryManager.registerInventoryForPlayer(bedwarsPlayer, shopInventory);
                shopInventory.open(player);
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
