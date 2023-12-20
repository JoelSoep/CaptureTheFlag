package me.joelsoep.ctf.map.type;

import lombok.Getter;
import lombok.Setter;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.map.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rotatable;
import org.jetbrains.annotations.TestOnly;

import java.io.File;

public abstract class GameMap extends Map {

    protected final String name;
    protected final File schematic;

    private final Location redFlagLocation;
    private final Location blueFlagLocation;
    private final Location redSpawnLocation;
    private final Location blueSpawnLocation;
    private final Integer maxPlayers;

    public GameMap(String name, File schematic) {
        super(name, "game_");
        this.name = name;
        this.schematic = schematic;
        this.redFlagLocation = getRedFlagLocation();
        this.blueFlagLocation = getBlueFlagLocation();
        this.redSpawnLocation = getRedSpawnLocation();
        this.blueSpawnLocation = getBlueSpawnLocation();
        this.maxPlayers = getMaxPlayers();

        this.setWorld(createEmptyWorld());
    }

    public abstract void load();
    public abstract Location getRedFlagLocation();
    public abstract Location getBlueFlagLocation();
    public abstract Location getRedSpawnLocation();
    public abstract Location getBlueSpawnLocation();
    public abstract Integer getMaxPlayers();


    @TestOnly
    public void placeFlag(Team team) {
        Location flagLocation = team.getFlag().getLocation();
        Material material = team.getFlag().getMaterial();
        BlockFace blockFace = team.getFlag().getBlockFace();
        Block block = flagLocation.getBlock();
        block.setType(material);
        BlockData blockData = block.getBlockData();
        if (blockData instanceof Rotatable rotatable) {
            rotatable.setRotation(blockFace);
            block.setBlockData(rotatable);
        }
    }

    @TestOnly
    public void removeFlag(Team team) {
        Location flagLocation = team.getFlag().getLocation();
        Block block = flagLocation.getBlock();
        block.setType(Material.AIR);
    }
}
