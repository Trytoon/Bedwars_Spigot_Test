package fr.trytoon.bedwars.commands;

import org.bukkit.command.CommandSender;

public interface CommandExecutor {
    /**
     *
     * @param sender - the sender data
     * @param args - the args of the command
     * @return the success status of the command
     */
    boolean execute(CommandSender sender, String[] args);
}
