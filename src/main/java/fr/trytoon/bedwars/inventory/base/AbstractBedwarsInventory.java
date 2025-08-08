package fr.trytoon.bedwars.inventory.base;

import fr.trytoon.bedwars.player.BedwarsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractBedwarsInventory {

    protected Inventory inventory;
    protected String title;
    protected int size;

    public AbstractBedwarsInventory() {

    }

    public AbstractBedwarsInventory(String title, int size) {
        this.title = title;
        this.size = size;
        this.inventory = Bukkit.createInventory(null, size, title);
    }

    public abstract void createInventory();

    public void refreshInventory() {
    }

    public void handleClick(BedwarsPlayer player, int slot, ItemStack item, Inventory inventory) {
    }

    public void open(Player player) {
        if (inventory != null) {
            player.openInventory(inventory);
        }
    }

    public Inventory getInventory() {
        return inventory;
    }
}
