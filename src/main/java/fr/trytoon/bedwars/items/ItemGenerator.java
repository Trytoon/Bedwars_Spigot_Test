package fr.trytoon.bedwars.items;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;

public class ItemGenerator {
    private final Location location;
    private final BedwarsItem item;

    private int tick;
    private int spawnDelay; //in ticks
    private int maxItems;

    public ItemGenerator(BedwarsItem item, int delay, Location location, int maxItems) {
        this.item = item;
        this.spawnDelay = secondsToTick(delay);
        this.location = location;
        this.maxItems = maxItems;
        this.tick = 0;
    }

    public void tick() {
        tick++;

        if (tick == spawnDelay) {
            spawnItem();
            tick = 0;
        }
    }

    public void changeDelay(int newDelay) {
        this.spawnDelay = newDelay;
    }

    private void spawnItem() {

        World world = location.getWorld();
        int nearbyItems = (int) world.getNearbyEntities(location, 5, 5, 5).stream()
                .filter(entity -> {
                    if (entity instanceof Item itemEntity) {
                        return itemEntity.getItemStack().isSimilar(this.item.toItemStack());
                    }
                    return false;
                })
                .count();

        if (nearbyItems < maxItems) {
            world.dropItemNaturally(location, item.toItemStack());
        }

    }

    private int secondsToTick(int seconds) {
        return seconds * 20;
    }

}
