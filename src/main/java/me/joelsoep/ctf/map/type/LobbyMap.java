package me.joelsoep.ctf.map.type;

import lombok.Setter;
import me.joelsoep.ctf.map.Map;
import org.bukkit.Location;
import org.bukkit.World;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;

public abstract class LobbyMap extends Map {

    protected Location spawnLocation;
    protected ArrayList<Location> leaderboardLocations;

    public LobbyMap(String name, World world) {
        super(name, "lobby_");

        this.setWorld(world);
        load();
    }

    protected abstract void load();
    protected abstract Location getSpawnLocation();
    protected abstract ArrayList<Location> getLeaderboardLocations();
}
