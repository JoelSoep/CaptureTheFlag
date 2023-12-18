package me.joelsoep.ctf.map.type.game;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.map.MapManager;
import me.joelsoep.ctf.map.type.GameMap;
import me.joelsoep.ctf.util.FAWE;
import org.bukkit.Location;

import java.io.File;

public class AirshipMap extends GameMap {
    public AirshipMap() {
        super("airship", new File(MapManager.getSchematicFolderPath() + "airship.schem"));
    }

    @Override
    public void load() {
        FAWE.paste(this.schematic, new Location(getWorld(), 0, 50, 0));

    }

    @Override
    public Location getRedFlagLocation() {
        return new Location(this.getWorld(), -117, -34, -50);
    }

    @Override
    public Location getBlueFlagLocation() {
        return new Location(getWorld(), -117, -34, -145);
    }

    @Override
    public Location getRedSpawnLocation() {
        return new Location(getWorld(), -70, -26.5, -49.5, -180, 0);
    }

    @Override
    public Location getBlueSpawnLocation() {
        return new Location(getWorld(), -70, -26.5, -144.5);
    }

    @Override
    public Integer getMaxPlayers() {
        return 20;
    }
}
