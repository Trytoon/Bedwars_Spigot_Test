package fr.trytoon.bedwars.inventory;

import fr.trytoon.bedwars.events.PlayerTeamJoinEvent;
import fr.trytoon.bedwars.events.PlayerTeamSelectEvent;
import fr.trytoon.bedwars.events.TeamCreatedEvent;
import fr.trytoon.bedwars.events.TeamRemovedEvent;
import fr.trytoon.bedwars.teams.BedwarsTeam;
import fr.trytoon.bedwars.teams.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamSelectorInventory implements BedwarsInventory {
    TeamManager teamManager;
    Inventory teamSelectorInventory;

    Map<Integer, BedwarsTeam> integerTeamMap;

    public TeamSelectorInventory(TeamManager teamManager) {
        this.teamManager = teamManager;
        this.teamSelectorInventory = createInventory();
    }

    public Inventory createInventory() {
        integerTeamMap = new HashMap<>();
        Inventory inventory = Bukkit.createInventory(null, 9, "Choisissez votre equipe!");

        int i = 0;
        for (BedwarsTeam t : teamManager.getTeams().values()) {
            ItemStack item = createSelectorObjectForTeam(t);

            integerTeamMap.put(i, t);

            inventory.setItem(i, item);
            i++;
        }

        return inventory;
    }
    public ItemStack createSelectorObjectForTeam(BedwarsTeam bedwarsTeam) {
        ItemStack item = bedwarsTeam.getTeamColor().getWool();
        ItemMeta meta = item.getItemMeta();

        String title = createItemTitle(bedwarsTeam);
        meta.setDisplayName(title);

        List<String> lore = createItemLore(bedwarsTeam);
        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }

    public void updateTeamItem(int index) {
        if (index > teamSelectorInventory.getSize()) {
            return;
        }

        ItemStack item = teamSelectorInventory.getItem(index);
        if (item != null) {
            BedwarsTeam bedwarsTeam = integerTeamMap.get(index);
            List<String> lore = createItemLore(bedwarsTeam);

            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setLore(lore);

                String title = createItemTitle(bedwarsTeam);
                meta.setDisplayName(title);

                item.setItemMeta(meta);
            }
        }
    }

    public List<String> createItemLore(BedwarsTeam bedwarsTeam) {
        ChatColor teamChatColor = bedwarsTeam.getTeamColor().getChatColor();

        List<String> lore = new ArrayList<>();
        lore.add("");

        int i = 0;
        for (Player player : bedwarsTeam.getPlayers()) {
            lore.add(ChatColor.GRAY +">> " + teamChatColor + player.getName());
            i++;
        }

        while (i < bedwarsTeam.getMaxMembers()) {
            lore.add(ChatColor.GRAY +">> -");
            i++;
        }

        lore.add("");
        lore.add(ChatColor.GRAY +"Cliquez pour rejoindre l'équipe " + teamChatColor + bedwarsTeam.getName() + ChatColor.GRAY + " !");
        return lore;
    }

    public String createItemTitle(BedwarsTeam bedwarsTeam) {
        ChatColor teamChatColor = bedwarsTeam.getTeamColor().getChatColor();
        return teamChatColor + "Equipe " + bedwarsTeam.getName() + ChatColor.GRAY +" (" + bedwarsTeam.getPlayersCount() + "/" + bedwarsTeam.getMaxMembers() +")";
    }


    @EventHandler
    public void onPlayerAdded(PlayerTeamJoinEvent event) {
        for (int index : integerTeamMap.keySet()) {
            updateTeamItem(index);
        }
    }

    @EventHandler
    public void onTeamCreated(TeamCreatedEvent event) {
        this.teamSelectorInventory = createInventory();
    }

    @EventHandler
    public void onTeamRemoved(TeamRemovedEvent event) {
        this.teamSelectorInventory = createInventory();
    }

    @Override
    @EventHandler
    public void handleInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();

        if (inv.equals(this.teamSelectorInventory)) {
            event.setCancelled(true);

            if (item != null && item.hasItemMeta()) {

                BedwarsTeam bedwarsTeam = this.getTeamFromIndex(event.getSlot());

                if (bedwarsTeam != null) {
                    PlayerTeamSelectEvent joinEvent = new PlayerTeamSelectEvent(player, bedwarsTeam.getName());
                    Bukkit.getServer().getPluginManager().callEvent(joinEvent);
                }
            }
        }
    }

    @Override
    @EventHandler
    public void handlePlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player != null) {
            ItemStack it = event.getItem();

            if (it != null && it.getType() == Material.DIRT) {
                openInventory(player);
            }
        }
    }

    public Inventory getInventory() {
        return this.teamSelectorInventory;
    }

    public BedwarsTeam getTeamFromIndex(int i) {
        return integerTeamMap.get(i);
    }
}

