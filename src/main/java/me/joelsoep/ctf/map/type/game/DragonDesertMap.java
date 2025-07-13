package me.joelsoep.ctf.map.type.game;

import me.joelsoep.ctf.file.FileManager;
import me.joelsoep.ctf.map.type.GameMap;
import me.joelsoep.ctf.util.FAWE;
import org.bukkit.Location;
import org.bukkit.Material;
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
    protected Location redFlagLocation() {
        return new Location(getWorld(), 0, 63, -121);
    }

    @Override
    protected Location redSpawnLocation() {
        return new Location(getWorld(), 0, 50, -121);
    }

    @Override
    protected BlockFace redBlockFace() {
        return BlockFace.SOUTH;
    }

    @Override
    protected Location blueFlagLocation() {
        return new Location(getWorld(), 0, 63, 122);
    }

    @Override
    protected Location blueSpawnLocation() {
        return new Location(getWorld(), 0, 50, 122, -180, 0);
    }

    @Override
    protected BlockFace blueBlockFace() {
        return BlockFace.NORTH;
    }

    @Override
    protected Location centerLocation() {
        return new Location(getWorld(), -3, 73, 7);
    }

    @Override
    protected int minPlayers() {
        return 8;
    }

    @Override
    protected int maxPlayers() {
        return 20;
    }

    @Override
    protected int worldSize() {
        return 300;
    }

    @Override
    protected int maxBuildHeight() {
        return 200;
    }

    @Override
    protected int minBuildHeight() {
        return -64;
    }

    @Override
    protected int deathHeight() {
        return -99;
    }

    @Override
    protected Material icon() {
        return Material.DEAD_BUSH;
    }
}
