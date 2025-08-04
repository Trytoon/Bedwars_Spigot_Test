package fr.trytoon.bedwars.commands;

import fr.trytoon.bedwars.teams.BedwarsTeam;
import fr.trytoon.bedwars.teams.TeamManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Locale;


public class TeamCommand implements CommandExecutor {

    TeamManager teamManager;

    public TeamCommand(TeamManager teamManager) {
        this.teamManager = teamManager;
    }
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        return switch (args[0].toUpperCase(Locale.ROOT)) {
            case "CREATE" -> createTeam(sender, args);
            case "REMOVE" -> removeTeam(sender, args);
            case "LIST" -> listTeams(sender, args);
            default -> false;
        };
    }

    private boolean listTeams(CommandSender sender, String[] args) {
        if (args.length != 1) {
            return false;
        }

        Collection<BedwarsTeam> bedwarsTeams = teamManager.getTeams().values();

        if (bedwarsTeams.isEmpty()) {
            sender.sendMessage("[BEDWARS]" + ChatColor.RED +"Aucune équipe définie.");
        }

        for (BedwarsTeam bedwarsTeam : bedwarsTeams) {
            ChatColor color = bedwarsTeam.getTeamColor().getChatColor();
            sender.sendMessage(color + bedwarsTeam.getName());
        }

        return true;
    }

    private boolean removeTeam(CommandSender sender, String[] args) {
        if (args.length != 2) {
            return false;
        }

        BedwarsTeam bedwarsTeam = teamManager.getTeamByName(args[1]);

        if (bedwarsTeam == null) {
            sender.sendMessage("[BEDWARS]" + ChatColor.RED + "L'équipe" + args[1] + " n'existe pas.");
        }
        else {
            boolean success = teamManager.deleteTeam(bedwarsTeam.getName());

            if (!success) {
                sender.sendMessage("[BEDWARS]" + ChatColor.RED + "Impossible de supprimer l'équipe " + bedwarsTeam.getName());
            } else {
                sender.sendMessage("[BEDWARS]" + " Equipe supprimée avec succès: "+ bedwarsTeam.getTeamColor().getChatColor() + bedwarsTeam.getName());
            }
        }

        return true;
    }

    private boolean createTeam(CommandSender sender, String[] args) {
        String teamName = args[1];

        if (teamManager.getTeamByName(teamName) != null) {
            sender.sendMessage("[BEDWARS]" + " Erreur en créant l'équipe: " + teamName + ". Equipe deja existante !");
        }

        if (sender instanceof Player) {
            if (args.length != 3) {
                return false;
            }

            int members;

            try {
                members = Integer.parseInt(args[2]);
            } catch (Exception e) {
                sender.sendMessage("[BEDWARS] Le nombre de joueurs doit etre un nombre");
                return false;
            }

            Player player = (Player) sender;
            Location spawnLocation = player.getLocation();

            boolean success = teamManager.createTeam(teamName, members, spawnLocation);

            if (success) {
                sender.sendMessage("[BEDWARS]" + " Equipe créée avec succes: " + teamName);
            }
            else {
                sender.sendMessage("[BEDWARS]" + " Erreur en créant l'équipe: " + teamName);
            }
        }

        return true;
    }
}
