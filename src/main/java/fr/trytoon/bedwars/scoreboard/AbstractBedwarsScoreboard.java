package fr.trytoon.bedwars.scoreboard;

import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class AbstractBedwarsScoreboard {

    protected final FastBoard board;
    protected final Player player;


    public AbstractBedwarsScoreboard(Player player) {
        this.player = player;
        this.board = new FastBoard(player);
        this.board.updateTitle(ChatColor.YELLOW + "BEDWARS");
    }

    protected abstract List<String> getLines();

    public void update() {
        this.board.updateLines(getLines());
    }

    public void delete() {
        if (!this.board.isDeleted()) {
            this.board.delete();
        }
    }

    public FastBoard getFastBoard() {
        return this.board;
    }
}