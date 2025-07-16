package me.joelsoep.ctf.map.type.game;

import me.joelsoep.ctf.file.FileManager;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.map.type.GameMap;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.FAWE;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.List;

public class CelestialMap extends GameMap {

    public CelestialMap() {
        super("celestial", new File(FileManager.getSchematicFolderPath() + "space_invaders.schem"));
    }

    @Override
    public void load() {
        clearEntities();
        FAWE.paste(this.schematic, new Location(getWorld(), 0, 50, 0));
        clearOpenings();
        placeBoostPads();
        placeHeavyBoostPads();
    }

    private void clearOpenings() {
        List<Location> locationsToClear = List.of(
                new Location(getWorld(), -51, 20, -27),
                new Location(getWorld(), -51, 21, -27),
                new Location(getWorld(), -51, 22, -27),
                new Location(getWorld(), -51, 20, -28),
                new Location(getWorld(), -51, 21, -28),
                new Location(getWorld(), -51, 22, -28),

                new Location(getWorld(), -43, 20, -27),
                new Location(getWorld(), -43, 21, -27),
                new Location(getWorld(), -43, 22, -27),
                new Location(getWorld(), -43, 20, -28),
                new Location(getWorld(), -43, 21, -28),
                new Location(getWorld(), -43, 22, -28),

                new Location(getWorld(), -43, 20, -281),
                new Location(getWorld(), -43, 21, -281),
                new Location(getWorld(), -43, 22, -281),
                new Location(getWorld(), -43, 20, -280),
                new Location(getWorld(), -43, 21, -280),
                new Location(getWorld(), -43, 22, -280),

                new Location(getWorld(), -51, 20, -281),
                new Location(getWorld(), -51, 21, -281),
                new Location(getWorld(), -51, 22, -281),
                new Location(getWorld(), -51, 20, -280),
                new Location(getWorld(), -51, 21, -280),
                new Location(getWorld(), -51, 22, -280)
        );

        for (Location location : locationsToClear) {
            Block block = location.getBlock();
            block.setType(Material.AIR);
        }
    }

    public void placeBoostPads() {
        List<Location> locations = List.of(
                new Location(getWorld(), -20, 20, -101),
                new Location(getWorld(), -74, 20, -207)
        );

        for (Location location : locations) {
            setBoostPadItem(location);
        }
    }

    public void placeHeavyBoostPads() {
        List<Location> locations = List.of(
                new Location(getWorld(), -15, 2, -184),
                new Location(getWorld(), -79, 2, -124)
        );

        for (Location location : locations) {
            setHeavyBoostPadItem(location);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!event.hasChangedBlock()) {
            return;
        }

        Location location = event.getTo().toCenterLocation();

        CTFPlayer ctfPlayer = PlayerService.getPlayer(player);
        if (ctfPlayer == null) {
            return;
        }

        Game game = ctfPlayer.getGame();
        if (game == null) {
            return;
        }

        if (!player.getWorld().getName().equals(this.getWorld().getName())) {
            return;
        }

        if (location.getBlock().getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
            player.setVelocity(player.getLocation().getDirection().add(new Vector(0, 1, 0)).multiply(3.4));
        }

        if (location.getBlock().getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE || location.getBlock().getType() == Material.GOLD_BLOCK) {
            player.setVelocity(player.getLocation().getDirection().add(new Vector(0, 2, 0)).multiply(5));
        }

    }

    private void setHeavyBoostPadItem(Location location) {
        location.getBlock().setType(Material.IRON_BLOCK);
        location.clone().add(0, 1, 0).getBlock().setType(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
        location.clone().add(1, 0, 0).getBlock().setType(Material.RAW_GOLD_BLOCK);
        location.clone().add(0, 0, 1).getBlock().setType(Material.RAW_GOLD_BLOCK);
        location.clone().subtract(1, 0,0).getBlock().setType(Material.RAW_GOLD_BLOCK);
        location.clone().subtract(0, 0, 1).getBlock().setType(Material.RAW_GOLD_BLOCK);
    }

    private void setBoostPadItem(Location location) {
        location.getBlock().setType(Material.GOLD_BLOCK);
        location.clone().add(0, 1, 0).getBlock().setType(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
        location.clone().add(1, 0, 0).getBlock().setType(Material.RAW_GOLD_BLOCK);
        location.clone().add(0, 0, 1).getBlock().setType(Material.RAW_GOLD_BLOCK);
        location.clone().subtract(1, 0,0).getBlock().setType(Material.RAW_GOLD_BLOCK);
        location.clone().subtract(0, 0, 1).getBlock().setType(Material.RAW_GOLD_BLOCK);
    }

    @Override
    public void resetFeatures() {

    }

    @Override
    protected Location redFlagLocation() {
        return new Location(getWorld(), -47, 30, -286);
    }

    @Override
    protected Location redSpawnLocation() {
        return new Location(getWorld(), -47, 20, -281);
    }

    @Override
    protected BlockFace redBlockFace() {
        return BlockFace.SOUTH;
    }

    @Override
    protected Location blueFlagLocation() {
        return new Location(getWorld(), -47, 30, -22);
    }

    @Override
    protected Location blueSpawnLocation() {
        return new Location(getWorld(), -47, 20, -26, 180, 0);
    }

    @Override
    protected BlockFace blueBlockFace() {
        return BlockFace.NORTH;
    }

    @Override
    protected Location centerLocation() {
        return new Location(getWorld(), -47, 40, -153);
    }

    @Override
    protected int minPlayers() {
        return 10;
    }

    @Override
    protected int maxPlayers() {
        return 20;
    }

    @Override
    protected int worldSize() {
        return 450;
    }

    @Override
    protected int maxBuildHeight() {
        return 120;
    }

    @Override
    protected int minBuildHeight() {
        return -14;
    }

    @Override
    protected int deathHeight() {
        return -50;
    }

    @Override
    protected Material icon() {
        return Material.FIREWORK_STAR;
    }
}
