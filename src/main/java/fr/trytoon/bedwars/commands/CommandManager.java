package fr.trytoon.bedwars.commands;

import fr.trytoon.bedwars.BedwarsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    private final BedwarsPlugin plugin;
    private final Map<String, CommandExecutor> commandMap;

    public CommandManager(BedwarsPlugin plugin) {
        this.plugin = plugin;
        this.commandMap = new HashMap<>();

        registerCommands();
    }

    public void registerCommands() {
        commandMap.put("start", new StartCommand(plugin.getGameManager(), plugin.getPlayerManager(), plugin.getGeneratorManager()));
        commandMap.put("team", new TeamCommand(plugin.getTeamManager()));
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        CommandExecutor executor = commandMap.get(cmd.getName().toLowerCase());

        if (executor != null) {
            return executor.execute(sender, args);
        }

        return false;
    }
}
