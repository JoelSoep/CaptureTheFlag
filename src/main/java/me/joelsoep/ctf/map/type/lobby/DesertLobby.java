package me.joelsoep.ctf.map.type.lobby;

import me.joelsoep.ctf.file.FileManager;
import me.joelsoep.ctf.map.type.LobbyMap;
import me.joelsoep.ctf.util.FAWE;
import org.bukkit.Location;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DesertLobby extends LobbyMap {
    public DesertLobby() {
        super("desert", new File(FileManager.getSchematicFolderPath() + "lobby.schem"));
    }

    @Override
    public void load() {
        FAWE.paste(this.schematic, new Location(getWorld(), 0, 50, 0));
    }

    @Override
    protected Location spawnLocation() {
        return new Location(getWorld(), 16, 35, 13, 138, 0);
    }

    @Override
    protected ArrayList<Location> leaderboardLocations() {
        return new ArrayList<>(List.of(
                new Location(getWorld(), 5, 39, 3),
                new Location(getWorld(), 5, 39, -3),
                new Location(getWorld(), 0, 39, 3),
                new Location(getWorld(), 0, 39, -3),
                new Location(getWorld(), 8, 39, 12)
        ));
    }
}
