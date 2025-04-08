package me.joelsoep.ctf.map.type;

import lombok.Getter;
import lombok.Setter;
import me.joelsoep.ctf.map.Map;
import org.bukkit.Location;

import java.io.File;
import java.util.ArrayList;

@Getter
@Setter
public abstract class LobbyMap extends Map {

    protected final File schematic;
    protected Location spawnLocation;
    protected ArrayList<Location> leaderboardLocations;

    public LobbyMap(String name, File schematic) {
        super(name, "lobby_");
        this.schematic = schematic;
        this.setWorld(createEmptyWorld());

        this.spawnLocation = spawnLocation();
        this.leaderboardLocations = leaderboardLocations();
    }

    public abstract void load();
    protected abstract Location spawnLocation();
    protected abstract ArrayList<Location> leaderboardLocations();
}
