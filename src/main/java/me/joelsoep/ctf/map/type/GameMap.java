package me.joelsoep.ctf.map.type;

import me.joelsoep.ctf.Main;
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

public abstract class GameMap extends Map {

    protected final File schematic;

    public GameMap(String name, File schematic) {
        super(name, "game_");
        this.schematic = schematic;

        this.setWorld(createEmptyWorld());
        world.setSpawnLocation(getCenterLocation());
        new CleanItemsTask(world);
    }

    public abstract void load();
    public abstract void resetFeatures();
    public abstract @NotNull Location getRedFlagLocation();
    public abstract @NotNull Location getRedSpawnLocation();
    public abstract @NotNull BlockFace getRedBlockFace();
    public abstract @NotNull Location getBlueFlagLocation();
    public abstract @NotNull Location getBlueSpawnLocation();
    public abstract @NotNull BlockFace getBlueBlockFace();
    public abstract @NotNull Location getCenterLocation();
    public abstract @NotNull Integer getMinPlayers();
    public abstract @NotNull Integer getMaxPlayers();
    public abstract @NotNull Integer getWorldSize();
    public abstract @NotNull Integer getMaxBuildHeight();
    public abstract @NotNull Integer getDeathHeight();

    public boolean isLocationInMapSize(Location location) {
        if (location.getBlock().getY() > getMaxBuildHeight()) {
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
