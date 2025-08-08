package fr.trytoon.bedwars.config;

import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.HashMap;

public class CurrencyConfig {

    // === File Info ===
    private static final String DATA_FOLDER = "plugins/Bedwars";
    private static final String CURRENCIES_FILE = "currencies.yml";

    // === NBT Keys ===
    public static final String NBT_CURRENCY_ID = "bw_currency_id";

    // === YAML Keys ===
    private static final String YAML_KEY_ITEM = "item";
    private static final String YAML_KEY_NAME = "name";

    public static final HashMap<String, ItemStack> CURRENCIES = loadCurrencyItems();

    private static HashMap<String, ItemStack> loadCurrencyItems() {
        HashMap<String, ItemStack> result = new HashMap<>();

        File file = new File(DATA_FOLDER, CURRENCIES_FILE);
        if (!file.exists()) {
            Bukkit.getLogger().warning("[BEDWARS] currencies.yml not found!");
            return result;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (String key : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(key);
            if (section != null) {
                ItemStack currency = createCurrency(section, key);
                if (currency != null) {
                    result.put(key, currency);
                }
            }
        }

        return result;
    }

    private static ItemStack createCurrency(ConfigurationSection section, String key) {
        String itemName = section.getString(YAML_KEY_ITEM);
        String displayName = section.getString(YAML_KEY_NAME);

        if (itemName == null || displayName == null) return null;

        Material material = Material.getMaterial(itemName.toUpperCase());
        if (material == null) {
            Bukkit.getLogger().warning("[BEDWARS] Currency: invalid material: " + itemName);
            return null;
        }

        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
            item.setItemMeta(meta);
        }

        NBT.modify(item, nbt -> {
            nbt.setString(NBT_CURRENCY_ID, key);
        });

        return item;
    }
}
