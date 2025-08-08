package fr.trytoon.bedwars.inventory;

import de.tr7zw.changeme.nbtapi.NBT;
import fr.trytoon.bedwars.config.CurrencyConfig;
import fr.trytoon.bedwars.config.ItemConfig;
import fr.trytoon.bedwars.config.ShopConfig;
import fr.trytoon.bedwars.inventory.base.AbstractPaginatedBedwarsInventory;
import fr.trytoon.bedwars.inventory.base.InventoryPage;
import fr.trytoon.bedwars.items.NbtItemsUtils;
import fr.trytoon.bedwars.player.BedwarsPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ShopInventory extends AbstractPaginatedBedwarsInventory {

    public ShopInventory() {
        super();
        pages = ShopConfig.SHOPS;
        if (!this.pages.isEmpty()) {
            InventoryPage firstPage = this.pages.values().iterator().next();
            currentPageId = firstPage.id();
            createInventory();
        }
    }

    @Override
    protected void handlePaginatedItemClick(BedwarsPlayer bedwarsPlayer, int slot, ItemStack item, Inventory inventory) {
        String itemId = NBT.get(item, nbt -> (String) nbt.getString(ItemConfig.NBT_ITEM_ID));

        // if the item is from our bedwars plugin
        if (itemId != null && !itemId.isEmpty()) {
            Player player = bedwarsPlayer.getPlayer();
            Inventory playerInventory = player.getInventory();

            // get the currency nbt and item (iron, gold..)
            String currency = NBT.get(item, nbt -> (String) nbt.getString(ShopConfig.NBT_KEY_CURRENCY));
            ItemStack currencyItem = CurrencyConfig.CURRENCIES.get(currency);

            // get the cost and pay
            if (currencyItem != null) {
                int cost = NBT.get(item, nbt -> (int) nbt.getInteger(ShopConfig.NBT_KEY_COST));

                // simulate
                int simulationAmount = NbtItemsUtils.simulateRemove(playerInventory, currencyItem, cost);

                // pay
                if (simulationAmount >= cost) {
                    NbtItemsUtils.remove(playerInventory, currencyItem, cost);

                    ItemStack bedwarsItem = ItemConfig.ITEMS.get(itemId);

                    // always clone item
                    ItemStack itemClone = bedwarsItem.clone();

                    playerInventory.addItem(itemClone);
                    player.playSound(player.getLocation(), org.bukkit.Sound.ORB_PICKUP, 1.0F, 1.0F);
                } else {
                    int remaining = cost - simulationAmount;
                    player.getPlayer().sendMessage("Impossible d'acheter l'objet. Il vous manque " + remaining + " " + currency + ".");
                }
            }
        }
    }
}
