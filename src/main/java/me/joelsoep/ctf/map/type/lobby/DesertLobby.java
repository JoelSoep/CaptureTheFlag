package me.joelsoep.ctf.map.type.lobby;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.map.type.LobbyMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;

public class DesertLobby extends LobbyMap {
    public DesertLobby() {
        super("desert", Bukkit.getWorld("CaptureTheFlag"));
    }

    @Override
    protected void load() {

    }

    @Override
    protected Location getSpawnLocation() {
        return null;
    }

    @Override
    protected ArrayList<Location> getLeaderboardLocations() {
        return null;
    }
}
