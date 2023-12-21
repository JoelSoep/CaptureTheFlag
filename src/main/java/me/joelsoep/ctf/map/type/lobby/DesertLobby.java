package me.joelsoep.ctf.map.type.lobby;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.map.MapManager;
import me.joelsoep.ctf.map.type.LobbyMap;
import me.joelsoep.ctf.util.FAWE;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.util.ArrayList;

public class DesertLobby extends LobbyMap {
    public DesertLobby() {
        super("desert", new File(MapManager.getSchematicFolderPath() + "lobby.schem"));
    }

    @Override
    public void load() {
        FAWE.paste(this.schematic, new Location(getWorld(), 0, 50, 0));
    }

    @Override
    public Location getSpawnLocation() {
        return null;
    }

    @Override
    public ArrayList<Location> getLeaderboardLocations() {
        return null;
    }
}
