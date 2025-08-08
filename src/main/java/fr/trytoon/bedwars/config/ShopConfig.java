package fr.trytoon.bedwars.config;

import de.tr7zw.changeme.nbtapi.NBT;
import fr.trytoon.bedwars.inventory.base.InventoryPage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ShopConfig {

    // === NBT Keys ===
    public static final String NBT_KEY_TAB_PAGE_ID = "bw_page";
    public static final String NBT_KEY_CURRENCY = "bw_currency";
    public static final String NBT_KEY_COST = "bw_cost";

    // === YAML Keys ===
    private static final String YAML_TABS = "tabs";
    private static final String YAML_PAGES = "pages";
    private static final String YAML_TITLE = "title";
    private static final String YAML_SIZE = "size";
    private static final String YAML_ITEMS = "items";
    private static final String YAML_ITEM = "item";
    private static final String YAML_PAGE = "page";
    private static final String YAML_ITEM_ID = "item_id";
    private static final String YAML_AMOUNT = "amount";
    private static final String YAML_COST = "cost";
    private static final String YAML_CURRENCY = "currency";
    private static final String YAML_ENCHANTMENTS = "enchantments";
    private static final String YAML_ENCH_TYPE = "type";
    private static final String YAML_ENCH_LEVEL = "level";

    // === File Info ===
    private static final String DATA_FOLDER = "plugins/Bedwars";
    private static final String SHOPS_FILE = "shops.yml";

    public static final Map<String, InventoryPage> SHOPS = loadShopConfig();

    private static Map<String, InventoryPage> loadShopConfig() {
        Map<String, InventoryPage> result = new LinkedHashMap<>();

        File file = new File(DATA_FOLDER, SHOPS_FILE);
        if (!file.exists()) {
            Bukkit.getLogger().warning("[BEDWARS] shops.yml not found!");
            return result;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection tabs = config.getConfigurationSection(YAML_TABS + "." + YAML_ITEMS);
        ConfigurationSection pages = config.getConfigurationSection(YAML_PAGES);

        Map<Integer, ItemStack> tabButtons = createTabButtons(tabs);
        result = createPages(pages, tabButtons);

        return result;
    }

    private static Map<Integer, ItemStack> createTabButtons(ConfigurationSection tabsSection) {
        Map<Integer, ItemStack> tabButtons = new LinkedHashMap<>();

        if (tabsSection == null) return tabButtons;

        for (String slotKey : tabsSection.getKeys(false)) {
            try {
                int slot = Integer.parseInt(slotKey);
                ConfigurationSection tabConfig = tabsSection.getConfigurationSection(slotKey);

                if (tabConfig == null) continue;

                String itemType = tabConfig.getString(YAML_ITEM);
                String pageId = tabConfig.getString(YAML_PAGE);

                if (itemType == null || pageId == null) {
                    Bukkit.getLogger().warning("[BEDWARS] itemType or pageId is missing for slot: " + slotKey);
                    continue;
                }

                ItemStack itemStack = new ItemStack(Material.matchMaterial(itemType.toUpperCase()));
                ItemMeta meta = itemStack.getItemMeta();

                meta.setDisplayName(ChatColor.WHITE + pageId);
                itemStack.setItemMeta(meta);

                NBT.modify(itemStack, nbt -> {
                    nbt.setString(NBT_KEY_TAB_PAGE_ID, pageId);
                });

                tabButtons.put(slot, itemStack);

            } catch (NumberFormatException e) {
                Bukkit.getLogger().warning("[BEDWARS] Invalid slot key found in tabs section: " + slotKey);
            }
        }

        return tabButtons;
    }

    private static Map<String, InventoryPage> createPages(ConfigurationSection pages, Map<Integer, ItemStack> tabButtons) {
        Map<String, InventoryPage> result = new LinkedHashMap<>();

        if (pages == null) return result;

        for (String pageId : pages.getKeys(false)) {
            ConfigurationSection pageConfig = pages.getConfigurationSection(pageId);
            if (pageConfig == null) continue;

            String pageTitle = ChatColor.translateAlternateColorCodes('&', pageConfig.getString(YAML_TITLE, "Shop"));
            int pageSize = pageConfig.getInt(YAML_SIZE, 54);
            ConfigurationSection pageItemsSection = pageConfig.getConfigurationSection(YAML_ITEMS);

            Map<Integer, ItemStack> items = loadPageItems(pageItemsSection);

            // add tab buttons
            items.putAll(tabButtons);

            result.put(pageId, new InventoryPage(pageId, pageTitle, pageSize, items));
        }

        return result;
    }

    private static Map<Integer, ItemStack> loadPageItems(ConfigurationSection pageItemsSection) {
        Map<Integer, ItemStack> items = new LinkedHashMap<>();

        if (pageItemsSection == null) return items;

        for (String slotKey : pageItemsSection.getKeys(false)) {
            try {
                int slot = Integer.parseInt(slotKey);
                ConfigurationSection slotConfig = pageItemsSection.getConfigurationSection(slotKey);
                if (slotConfig == null) continue;

                ItemStack item = createItemFromConfig(slotConfig);
                if (item != null) {
                    items.put(slot, item);
                } else {
                    Bukkit.getLogger().warning("[BEDWARS] Item inconnu avec id: " + slotConfig.getString(YAML_ITEM_ID));
                }
            } catch (NumberFormatException e) {
                Bukkit.getLogger().warning("[BEDWARS] Impossible d'utiliser le slot n°: " + slotKey);
            }
        }

        return items;
    }

    private static ItemStack createItemFromConfig(ConfigurationSection slotConfig) {
        String itemId = slotConfig.getString(YAML_ITEM_ID);
        ItemStack baseItem = ItemConfig.ITEMS.get(itemId);

        if (baseItem == null) return null;

        ItemStack itemClone = baseItem.clone();
        int amount = slotConfig.getInt(YAML_AMOUNT, 1);
        int cost = slotConfig.getInt(YAML_COST, 0);
        String currency = slotConfig.getString(YAML_CURRENCY);

        itemClone.setAmount(amount);

        applyCostNBTAndLore(itemClone, cost, currency);
        applyEnchantments(itemClone, slotConfig.getList(YAML_ENCHANTMENTS));

        return itemClone;
    }

    private static void applyCostNBTAndLore(ItemStack item, int cost, String currency) {
        if (currency == null) return;

        ItemStack currencyItem = CurrencyConfig.CURRENCIES.get(currency);
        if (currencyItem == null) {
            Bukkit.getLogger().warning("[BEDWARS]: Monnaie inexistante: " + currency);
            return;
        }

        NBT.modify(item, nbt -> {
            nbt.setString(NBT_KEY_CURRENCY, currency);
            nbt.setInteger(NBT_KEY_COST, cost);

            nbt.modifyMeta((readOnlyNbt, meta) -> {
                String currencyDisplayName = currency;
                if (currencyItem.hasItemMeta() && currencyItem.getItemMeta().hasDisplayName()) {
                    currencyDisplayName = currencyItem.getItemMeta().getDisplayName();
                }

                List<String> lore = meta.getLore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }
                lore.add("");
                lore.add("§7Prix: " + cost + " " + currencyDisplayName);
                meta.setLore(lore);
            });
        });
    }


    private static void applyEnchantments(ItemStack item, List<?> enchantmentsList) {
        if (enchantmentsList == null) return;

        for (Object obj : enchantmentsList) {
            if (!(obj instanceof Map<?, ?> enchantmentMap)) continue;

            Object typeObj = enchantmentMap.get(YAML_ENCH_TYPE);
            Object levelObj = enchantmentMap.get(YAML_ENCH_LEVEL);

            if (!(typeObj instanceof String enchantmentType) || !(levelObj instanceof Number enchantmentLevelNumber)) continue;

            Enchantment enchantment = Enchantment.getByName(enchantmentType.toUpperCase());
            if (enchantment != null) {
                item.addEnchantment(enchantment, enchantmentLevelNumber.intValue());
            }
        }
    }
}
