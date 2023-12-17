package me.joelsoep.ctf.map.type;

import lombok.Setter;
import me.joelsoep.ctf.map.Map;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;

public abstract class GameMap extends Map {

    protected final String name;
    protected final File schematic;

    @Setter private Location flagLocation;
    @Setter private Location redSpawnLocation;
    @Setter private Location blueSpawnLocation;
    @Setter protected Integer maxPlayers;

    public GameMap(String name, File schematic) {
        super(name, "game_");
        this.name = name;
        this.schematic = schematic;

        this.setWorld(createEmptyWorld());
        load();
    }

    public abstract void load();
}
