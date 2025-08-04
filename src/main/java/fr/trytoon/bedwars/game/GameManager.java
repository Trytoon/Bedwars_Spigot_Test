package fr.trytoon.bedwars.game;

import fr.trytoon.bedwars.BedwarsPlugin;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    BedwarsPlugin plugin;
    GameState currentGameState;

    List<Block> placedBlocks;

    public GameManager(BedwarsPlugin plugin) {
        this.plugin = plugin;
        this.currentGameState = GameState.WAITING_FOR_PLAYER;
        this.placedBlocks = new ArrayList<>();
    }

    public GameState getCurrentGameState() {
        return this.currentGameState;
    }

    public void setCurrentGameState(GameState gameState) {
        this.currentGameState = gameState;
    }

    public boolean isBlockPlaced(Block block) {
        return placedBlocks.contains(block);
    }

    public boolean removePlacedBlockByPlayer(Block block) {
        return placedBlocks.remove(block);
    }

    public void addPlacedBlockByPlayer(Block block) {
        if (!isBlockPlaced(block)) {
            placedBlocks.add(block);
        }
    }

}
