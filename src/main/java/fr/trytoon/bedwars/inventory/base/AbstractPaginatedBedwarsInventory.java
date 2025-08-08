package fr.trytoon.bedwars.inventory.base;

import de.tr7zw.changeme.nbtapi.NBT;
import fr.trytoon.bedwars.config.ShopConfig;
import fr.trytoon.bedwars.player.BedwarsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractPaginatedBedwarsInventory extends AbstractBedwarsInventory {

    protected Map<String, InventoryPage> pages;
    protected String currentPageId;
    protected boolean changePage;

    public AbstractPaginatedBedwarsInventory() {
        this.pages = new LinkedHashMap<>();
        this.changePage = false;
    }

    @Override
    public void createInventory() {
        updateInventorySizeAndTitle();

        InventoryPage currentPage = pages.get(currentPageId);

        for (var entry : currentPage.items().entrySet()) {
            if (entry.getKey() < this.size && entry.getKey() >= 0) {
                inventory.setItem(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void handleClick(BedwarsPlayer player, int slot, ItemStack item, Inventory inventory) {
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        String targetPageId = NBT.get(item, nbt -> (String) nbt.getString(ShopConfig.NBT_KEY_TAB_PAGE_ID));

        if (targetPageId != null && !targetPageId.isEmpty()) {
            changePage(player, targetPageId);
            return;
        }

        handlePaginatedItemClick(player, slot, item, inventory);
    }

    protected abstract void handlePaginatedItemClick(BedwarsPlayer player, int slot, ItemStack item, Inventory inventory);

    protected void changePage(BedwarsPlayer player, String targetPageId) {
        if (pages.containsKey(targetPageId)) {
            changePage = true;
            this.currentPageId = targetPageId;
            createInventory();
            player.getPlayer().openInventory(inventory);
        }
    }

    private void updateInventorySizeAndTitle() {
        InventoryPage currentPage = pages.get(currentPageId);

        String newTitle = currentPage.title();
        int requiredRows = (int) Math.ceil((double) currentPage.items().size() / 9.0) + 1;
        int newSize = requiredRows * 9;

        this.size = Math.max(9, Math.min(newSize, 54));
        this.inventory = Bukkit.createInventory(null, this.size, newTitle + ChatColor.RESET);
    }

    public boolean isChangePage() {
        return changePage;
    }

    public void setChangePage(boolean changePage) {
        this.changePage = changePage;
    }
}