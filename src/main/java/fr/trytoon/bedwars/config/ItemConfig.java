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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ItemConfig {

    // === File Info ===
    private static final String DATA_FOLDER = "plugins/Bedwars";
    private static final String ITEMS_FILE = "items.yml";

    // === NBT Keys ===
    public static final String NBT_KEY_UNBREAKABLE = "Unbreakable";
    public static final String NBT_KEY_DROP = "drop";
    public static final String NBT_ITEM_ID = "bw_item_id";

    // === YAML Keys ===
    private static final String YAML_KEY_ITEM = "item";
    private static final String YAML_KEY_NAME = "name";
    private static final String YAML_KEY_LORE = "lore";
    private static final String YAML_KEY_UNBREAKABLE = "unbreakable";
    private static final String YAML_KEY_DROP = "drop";

    public static final Map<String, ItemStack> ITEMS = loadItems();

    private static Map<String, ItemStack> loadItems() {
        LinkedHashMap<String, ItemStack> result = new LinkedHashMap<>();

        File file = new File(DATA_FOLDER, ITEMS_FILE);
        if (!file.exists()) {
            Bukkit.getLogger().warning("[BEDWARS] items.yml not found!");
            return result;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (String key : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(key);
            ItemStack item = createBedwarsItem(section, key);
            if (item != null) {
                result.put(key, item);
            }
        }

        return result;
    }

    private static ItemStack createBedwarsItem(ConfigurationSection itemSection, String key) {
        String materialName = itemSection.getString(YAML_KEY_ITEM);
        if (materialName == null) {
            return null;
        }

        Material material = Material.getMaterial(materialName.toUpperCase());
        if (material == null) {
            return null;
        }

        String name = ChatColor.translateAlternateColorCodes('&', itemSection.getString(YAML_KEY_NAME, ""));
        List<String> lore = itemSection.getStringList(YAML_KEY_LORE);

        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (!name.isEmpty()) {
            itemMeta.setDisplayName(name);
        }
        if (!lore.isEmpty()) {
            List<String> translatedLore = new ArrayList<>();
            for (String line : lore) {
                translatedLore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            itemMeta.setLore(translatedLore);
        }

        itemStack.setItemMeta(itemMeta);

        boolean unbreakable = itemSection.getBoolean(YAML_KEY_UNBREAKABLE);
        boolean drop = itemSection.getBoolean(YAML_KEY_DROP);

        NBT.modify(itemStack, nbt -> {
            if (unbreakable) {
                nbt.setBoolean(NBT_KEY_UNBREAKABLE, true);
            }

            if (!drop) {
                nbt.setBoolean(NBT_KEY_DROP, false);
            }

            nbt.setString(NBT_ITEM_ID, key);
        });

        return itemStack;
    }
}
