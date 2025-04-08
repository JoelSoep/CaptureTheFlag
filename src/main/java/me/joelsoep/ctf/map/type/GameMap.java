package me.joelsoep.ctf.map.type;

import lombok.Getter;
import lombok.Setter;
import me.joelsoep.ctf.game.task.CleanItemsTask;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.game.team.TeamColor;
import me.joelsoep.ctf.map.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rotatable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
@Getter
@Setter
public abstract class GameMap extends Map {

    protected final File schematic;

    protected @NotNull Location redFlagLocation;
    protected @NotNull Location redSpawnLocation;
    protected @NotNull BlockFace redBlockFace;
    protected @NotNull Location blueFlagLocation;
    protected @NotNull Location blueSpawnLocation;
    protected @NotNull BlockFace blueBlockFace;
    protected @NotNull Location centerLocation;
    protected @NotNull Integer minPlayers;
    protected @NotNull Integer maxPlayers;
    protected @NotNull Integer worldSize;
    protected @NotNull Integer maxBuildHeight;
    protected @NotNull Integer minBuildHeight;
    protected @NotNull Integer deathHeight;

    public GameMap(String name, File schematic) {
        super(name, "game_");
        this.schematic = schematic;
        this.setWorld(createEmptyWorld());

        // set values for the map

        this.redFlagLocation = redFlagLocation();
        this.redSpawnLocation = redSpawnLocation();
        this.redBlockFace = redBlockFace();
        this.blueFlagLocation = blueFlagLocation();
        this.blueSpawnLocation = blueSpawnLocation();
        this.blueBlockFace = blueBlockFace();
        this.centerLocation = centerLocation();
        this.minPlayers = minPlayers();
        this.maxPlayers = maxPlayers();
        this.worldSize = worldSize();
        this.maxBuildHeight = maxBuildHeight();
        this.minBuildHeight = minBuildHeight();
        this.deathHeight = deathHeight();

        world.setSpawnLocation(this.centerLocation);
        new CleanItemsTask(world);
    }

    public abstract void load();
    public abstract void resetFeatures();
    protected abstract Location redFlagLocation();
    protected abstract Location redSpawnLocation();
    protected abstract BlockFace redBlockFace();
    protected abstract Location blueFlagLocation();
    protected abstract Location blueSpawnLocation();
    protected abstract BlockFace blueBlockFace();
    protected abstract Location centerLocation();
    protected abstract int minPlayers();
    protected abstract int maxPlayers();
    protected abstract int worldSize();
    protected abstract int maxBuildHeight();
    protected abstract int minBuildHeight();
    protected abstract int deathHeight();

    public boolean isLocationInMapSize(Location location) {
        if (location.getBlock().getY() > getMaxBuildHeight()) {
            return false;
        }

        if (location.getBlock().getY() < getMinBuildHeight()) {
            return false;
        }

        if (location.getBlock().getX() > getCenterLocation().getBlock().getX() + getWorldSize()) {
            return false;
        }

        if (location.getBlock().getX() < getCenterLocation().getBlock().getX() - getWorldSize()) {
            return false;
        }

        if (location.getBlock().getZ() > getCenterLocation().getBlock().getZ() + getWorldSize()) {
            return false;
        }

        if (location.getBlock().getZ() < getCenterLocation().getBlock().getZ() - getWorldSize()) {
            return false;
        }
        return true;
    }

    public void placeFlag(Team team) {
        Material material = team.getFlag().getMaterial();
        Location flagLocation;
        BlockFace blockFace = null;
        Block block = null;

        if (team.getTeamColor() == TeamColor.BLUE) {
            flagLocation = getBlueFlagLocation();
            blockFace = getBlueBlockFace();
            block = flagLocation.getBlock();
        }

        if (team.getTeamColor() == TeamColor.RED) {
            flagLocation = getRedFlagLocation();
            blockFace = getRedBlockFace();
            block = flagLocation.getBlock();
        }

        if (block == null) {
            return;
        }

        block.setType(material);
        BlockData blockData = block.getBlockData();
        if (blockData instanceof Rotatable rotatable) {
            rotatable.setRotation(blockFace);
            block.setBlockData(rotatable);
        }
    }

    public void removeFlag(Team team) {
        Location flagLocation = null;

        if (team.getTeamColor() == TeamColor.BLUE) {
            flagLocation = getBlueFlagLocation();
        }

        if (team.getTeamColor() == TeamColor.RED) {
            flagLocation = getRedFlagLocation();
        }

        if (flagLocation == null) {
            return;
        }

        Block block = flagLocation.getBlock();
        block.setType(Material.AIR);
    }
}
