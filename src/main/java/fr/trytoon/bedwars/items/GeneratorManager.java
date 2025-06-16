package fr.trytoon.bedwars.items;

import fr.trytoon.bedwars.BedwarsConstants;
import fr.trytoon.bedwars.BedwarsPlugin;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class GeneratorManager {
    private List<ItemGenerator> generators;
    private BedwarsPlugin plugin;
    private BukkitTask generatorTask;

    public GeneratorManager(BedwarsPlugin plugin) {
        generators = instanciateGenerators();
        this.plugin = plugin;
    }

    public void run() {
        generatorTask = new BukkitRunnable() {
            public void run() {
                for (ItemGenerator gen : generators) {
                    if (gen != null) {
                        gen.tick();
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void stop() {
        if (generatorTask != null) {
            generatorTask.cancel();
            generatorTask = null;
        }
    }

    //exemple de generators --> à deplacer vers le fichier de config
    private List<ItemGenerator> instanciateGenerators() {
        List<ItemGenerator> generators = new ArrayList<>();

        Location location = new Location(getServer().getWorld(BedwarsConstants.TEST_WORLD),  -150, 86, 2);
        Location location2 = new Location(getServer().getWorld(BedwarsConstants.TEST_WORLD),  -75, 86, 80);
        generators.add(new ItemGenerator(BedwarsItem.BRONZE, 1, location, 256));
        generators.add(new ItemGenerator(BedwarsItem.BRONZE, 1, location2, 256));

        return generators;
    }
}
