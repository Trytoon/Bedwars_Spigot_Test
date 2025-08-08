package fr.trytoon.bedwars.commands;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTType;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;
import fr.trytoon.bedwars.items.NbtItemsUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NbtCommand implements CommandExecutor{
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Cette commande ne peut être utilisée que par un joueur.");
            return true;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInHand();

        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "Vous ne tenez aucun objet.");
            return true;
        }

        ReadableNBT nbt = NBT.readNbt(item);

        StringBuilder nbtValues = new StringBuilder();
        for (String key : nbt.getKeys()) {
            NBTType type = nbt.getType(key);
            Object value = NbtItemsUtils.getValue(nbt, key);
            nbtValues.append(String.format("{%s: %s = %s}\n", key, type, value));
        }

        player.sendMessage(nbtValues.toString());

        return true;
    }
}
