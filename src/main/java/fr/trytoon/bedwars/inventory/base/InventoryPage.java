package fr.trytoon.bedwars.inventory.base;

import org.bukkit.inventory.ItemStack;

import java.util.Map;

public record InventoryPage(
    String id,
    String title,
    Integer size,
    Map<Integer, ItemStack> items
) {}
