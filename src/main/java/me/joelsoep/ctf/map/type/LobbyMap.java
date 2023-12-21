package me.joelsoep.ctf.map.type;

import lombok.Setter;
import me.joelsoep.ctf.map.Map;
import org.bukkit.Location;
import org.bukkit.World;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;

public abstract class LobbyMap extends Map {

    protected final File schematic;
    protected Location spawnLocation;
    protected ArrayList<Location> leaderboardLocations;

    public LobbyMap(String name, File schematic) {
        super(name, "lobby_");
        this.schematic = schematic;

        this.setWorld(createEmptyWorld());
    }

    public abstract void load();
    public abstract Location getSpawnLocation();
    public abstract ArrayList<Location> getLeaderboardLocations();
}
