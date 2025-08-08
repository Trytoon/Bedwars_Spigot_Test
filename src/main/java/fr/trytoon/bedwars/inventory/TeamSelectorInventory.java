package fr.trytoon.bedwars.inventory;

import fr.trytoon.bedwars.events.PlayerTeamChangeEvent;
import fr.trytoon.bedwars.inventory.base.AbstractBedwarsInventory;
import fr.trytoon.bedwars.player.BedwarsPlayer;
import fr.trytoon.bedwars.teams.BedwarsTeam;
import fr.trytoon.bedwars.teams.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamSelectorInventory extends AbstractBedwarsInventory implements Listener {
    private final TeamManager teamManager;

    Map<Integer, BedwarsTeam> integerTeamMap;

    public TeamSelectorInventory(TeamManager teamManager) {
        super("Choisissez votre equipe!", 9);
        this.teamManager = teamManager;
        this.integerTeamMap = new HashMap<>();

        createInventory();
    }

    @Override
    public void createInventory() {
        refreshInventory();
    }

    @Override
    public void refreshInventory() {
        int i = 0;
        for (BedwarsTeam t : teamManager.getTeams().values()) {
            ItemStack item = createSelectorObjectForTeam(t);

            integerTeamMap.put(i, t);

            inventory.setItem(i, item);
            i++;
        }
    }

    @Override
    public void handleClick(BedwarsPlayer bedwarsPlayer, int slot, ItemStack item, Inventory inventory) {
        if (inventory.equals(getInventory())) {
            if (item != null && item.hasItemMeta()) {

                BedwarsTeam currentBedwarsTeam = bedwarsPlayer.getTeam();
                BedwarsTeam newBedwarsTeam = integerTeamMap.get(slot);

                if (newBedwarsTeam != null) {
                    PlayerTeamChangeEvent playerTeamChangeEvent = new PlayerTeamChangeEvent(bedwarsPlayer, currentBedwarsTeam, newBedwarsTeam);
                    Bukkit.getServer().getPluginManager().callEvent(playerTeamChangeEvent);
                }
            }
        }
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

    public List<String> createItemLore(BedwarsTeam bedwarsTeam) {
        ChatColor teamChatColor = bedwarsTeam.getTeamColor().getChatColor();

        List<String> lore = new ArrayList<>();
        lore.add("");

        int i = 0;
        for (BedwarsPlayer bedwarsPlayer : bedwarsTeam.getPlayers()) {
            lore.add(ChatColor.GRAY +">> " + teamChatColor + bedwarsPlayer.getPlayer().getName());
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
}
