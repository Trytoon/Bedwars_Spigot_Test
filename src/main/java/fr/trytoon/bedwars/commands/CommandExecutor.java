package fr.trytoon.bedwars.commands;

import org.bukkit.command.CommandSender;

public interface CommandExecutor {
    boolean execute(CommandSender sender, String[] args);
}
