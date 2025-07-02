package me.joelsoep.ctf.map.type.game;

import me.joelsoep.ctf.file.FileManager;
import me.joelsoep.ctf.map.type.GameMap;
import me.joelsoep.ctf.util.FAWE;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.io.File;

public class SpaceInvadersMap extends GameMap {

    public SpaceInvadersMap() {
        super("Space_Invaders", new File(FileManager.getSchematicFolderPath() + "space_invaders.schem"));
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
        return new Location(getWorld(), -46, 30, -285);
    }

    @Override
    protected Location redSpawnLocation() {
        return new Location(getWorld(), -46, 20, -281);
    }

    @Override
    protected BlockFace redBlockFace() {
        return BlockFace.SOUTH;
    }

    @Override
    protected Location blueFlagLocation() {
        return new Location(getWorld(), -46, 30, -21);
    }

    @Override
    protected Location blueSpawnLocation() {
        return new Location(getWorld(), -46, 20, -25, 180, 0);
    }

    @Override
    protected BlockFace blueBlockFace() {
        return BlockFace.NORTH;
    }

    @Override
    protected Location centerLocation() {
        return new Location(getWorld(), -46, 40, -153);
    }

    @Override
    protected int minPlayers() {
        return 10;
    }

    @Override
    protected int maxPlayers() {
        return 20;
    }

    @Override
    protected int worldSize() {
        return 450;
    }

    @Override
    protected int maxBuildHeight() {
        return 120;
    }

    @Override
    protected int minBuildHeight() {
        return -14;
    }

    @Override
    protected int deathHeight() {
        return -50;
    }
}
