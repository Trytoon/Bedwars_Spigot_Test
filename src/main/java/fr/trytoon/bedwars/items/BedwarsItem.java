package fr.trytoon.bedwars.items;

import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class BedwarsItem {

    public static BedwarsItem SILVER = new BedwarsItem(
        Material.IRON_INGOT,
        "silver",
        "Silver",
        null,
        1
    );

    public static BedwarsItem BRONZE = new BedwarsItem(
            Material.CLAY_BRICK,
            "bronze",
            "Bronze",
            null,
            1
    );

    public static BedwarsItem TEAM_SELECTOR = new BedwarsItem(
            Material.COMPASS,
            "team-selector",
            "Selection d'équipes",
            null,
            1
    );

    private final Material material;
    private final String customId;
    private final String displayName;
    private final List<String> lore;
    private final int amount;

    public BedwarsItem(Material material, String customId, String displayName, List<String> lore, int amount) {
        this.material = material;
        this.customId = customId;
        this.displayName = displayName;
        this.lore = lore;
        this.amount = amount;
    }

    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(material, amount);

        NBT.modify(item, nbt -> {
            nbt.setString("bedwars-id", customId);
        });

        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(displayName);

            if (lore != null && !lore.isEmpty()) {
                meta.setLore(lore);
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    public static boolean isBedwarsItem(ItemStack item, String expectedId) {
        AtomicReference<Boolean> match = new AtomicReference<>(false);

        NBT.modify(item, nbt -> {
            if (nbt.hasTag("bedwars-id")) {
                match.set(expectedId.equals(nbt.getString("bedwars-id")));
            }
        });

        return match.get();
    }

    public static String getCustomId(ItemStack item) {
        if (item == null) return null;

        AtomicReference<String> id = new AtomicReference<>(null);

        NBT.modify(item, nbt -> {
            if (nbt.hasTag("bedwars-id")) {
                id.set(nbt.getString("bedwars-id"));
            }
        });

        return id.get();
    }
}
