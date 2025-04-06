package me.joelsoep.ctf.map.type.game;

import me.joelsoep.ctf.file.FileManager;
import me.joelsoep.ctf.map.type.GameMap;
import me.joelsoep.ctf.util.FAWE;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.io.File;

public class DragonDesertMap extends GameMap {
    public DragonDesertMap() {
        super("dragon_desert", new File(FileManager.getSchematicFolderPath() + "dragondesert.schem"));
    }

    @Override
    public void load() {
        clearEntities();
        FAWE.paste(this.schematic, new Location(getWorld(), 0, 50, 0));
    }

    @Override
    public void resetFeatures() {

    }

    @Override
    protected void redFlagLocation() {
        redFlagLocation = new Location(getWorld(), 0, 63, -121);
    }

    @Override
    protected void redSpawnLocation() {
        redSpawnLocation = new Location(getWorld(), 0, 50, -121);
    }

    @Override
    protected void redBlockFace() {
        redBlockFace = BlockFace.SOUTH;
    }

    @Override
    protected void blueFlagLocation() {
        blueFlagLocation = new Location(getWorld(), 0, 63, 122);
    }

    @Override
    protected void blueSpawnLocation() {
        blueSpawnLocation = new Location(getWorld(), 0, 50, 122);
    }

    @Override
    protected void blueBlockFace() {
        blueBlockFace = BlockFace.NORTH;
    }

    @Override
    protected void centerLocation() {
        centerLocation = new Location(getWorld(), -3, 73, 7);
    }

    @Override
    protected void minPlayers() {
        minPlayers = 8;
    }

    @Override
    protected void maxPlayers() {
        maxPlayers = 20;
    }

    @Override
    protected void worldSize() {
        worldSize = 300;
    }

    @Override
    protected void maxBuildHeight() {
        maxBuildHeight = 200;
    }

    @Override
    protected void deathHeight() {
        deathHeight = -99;
    }
}
