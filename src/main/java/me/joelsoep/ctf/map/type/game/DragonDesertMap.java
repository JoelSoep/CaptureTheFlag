package me.joelsoep.ctf.map.type.game;

import me.joelsoep.ctf.file.FileManager;
import me.joelsoep.ctf.map.type.GameMap;
import me.joelsoep.ctf.util.FAWE;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

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
    public @NotNull Location getRedFlagLocation() {
        return new Location(getWorld(), 0, 63, -121);
    }

    @Override
    public @NotNull Location getRedSpawnLocation() {
        return new Location(getWorld(), 0, 50, -121);
    }

    @Override
    public @NotNull BlockFace getRedBlockFace() {
        return BlockFace.SOUTH;
    }

    @Override
    public @NotNull Location getBlueFlagLocation() {
        return new Location(getWorld(), 0, 63, 122);
    }

    @Override
    public @NotNull Location getBlueSpawnLocation() {
        return new Location(getWorld(), 0, 50, 122);
    }

    @Override
    public @NotNull BlockFace getBlueBlockFace() {
        return BlockFace.NORTH;
    }

    @Override
    public @NotNull Location getCenterLocation() {
        return new Location(getWorld(), -3, 73, 7);
    }

    @Override
    public @NotNull Integer getMinPlayers() {
        return 8;
    }

    @Override
    public @NotNull Integer getMaxPlayers() {
        return 20;
    }

    @Override
    public @NotNull Integer getWorldSize() {
        return 300;
    }

    @Override
    public @NotNull Integer getMaxBuildHeight() {
        return 200;
    }

    @Override
    public @NotNull Integer getDeathHeight() {
        return -99;
    }
}
