package fr.trytoon.bedwars.teams;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TeamColor {
    final String colorName;

    private static final Map<String, Short> COLOR_TO_ID = new HashMap<>();
    private static final Map<String, ChatColor> COLOR_TO_CHAT_COLOR = new HashMap<>();

    static {
        COLOR_TO_ID.put("WHITE", (short) 0);
        COLOR_TO_ID.put("ORANGE", (short) 1);
        COLOR_TO_ID.put("LIGHT_BLUE", (short) 3);
        COLOR_TO_ID.put("YELLOW", (short) 4);
        COLOR_TO_ID.put("LIME", (short) 5);
        COLOR_TO_ID.put("PINK", (short) 6);
        COLOR_TO_ID.put("GRAY", (short) 7);
        COLOR_TO_ID.put("LIGHT_GRAY", (short) 8);
        COLOR_TO_ID.put("CYAN", (short) 9);
        COLOR_TO_ID.put("PURPLE", (short) 10);
        COLOR_TO_ID.put("BLUE", (short) 11);
        COLOR_TO_ID.put("GREEN", (short) 13);
        COLOR_TO_ID.put("RED", (short) 14);
        COLOR_TO_ID.put("BLACK", (short) 15);
    }

    static {
        COLOR_TO_CHAT_COLOR.put("WHITE", ChatColor.WHITE);
        COLOR_TO_CHAT_COLOR.put("ORANGE", ChatColor.GOLD);
        COLOR_TO_CHAT_COLOR.put("LIGHT_BLUE", ChatColor.AQUA);
        COLOR_TO_CHAT_COLOR.put("YELLOW", ChatColor.YELLOW);
        COLOR_TO_CHAT_COLOR.put("LIME", ChatColor.GREEN);
        COLOR_TO_CHAT_COLOR.put("PINK", ChatColor.LIGHT_PURPLE);
        COLOR_TO_CHAT_COLOR.put("GRAY", ChatColor.DARK_GRAY);
        COLOR_TO_CHAT_COLOR.put("LIGHT_GRAY", ChatColor.GRAY);
        COLOR_TO_CHAT_COLOR.put("CYAN", ChatColor.DARK_AQUA);
        COLOR_TO_CHAT_COLOR.put("PURPLE", ChatColor.DARK_PURPLE);
        COLOR_TO_CHAT_COLOR.put("BLUE", ChatColor.BLUE);
        COLOR_TO_CHAT_COLOR.put("GREEN", ChatColor.DARK_GREEN);
        COLOR_TO_CHAT_COLOR.put("RED", ChatColor.RED);
        COLOR_TO_CHAT_COLOR.put("BLACK",  ChatColor.BLACK);
    }

    public TeamColor(String name) {
        this.colorName = getColorName(name);
    }

    public ItemStack getWool() {
        short type = COLOR_TO_ID.get(colorName);
        return new ItemStack(Material.WOOL, 1, type);
    }

    public ChatColor getChatColor() {
        return COLOR_TO_CHAT_COLOR.get(colorName);
    }

    private String getColorName(String colorName) {
        colorName = colorName.toUpperCase(Locale.ROOT);
        if (COLOR_TO_ID.get(colorName) == null) {
            Bukkit.getLogger().warning("[BEDWARS] Cannot parse color: "+ colorName +". Setting default color to WHITE");
            return "WHITE";
        }

        return colorName;

    }
}
