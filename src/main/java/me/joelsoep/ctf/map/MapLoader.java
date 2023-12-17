package me.joelsoep.ctf.map;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.special.SpecialService;
import me.joelsoep.ctf.util.FAWE;
import me.joelsoep.ctf.game.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rotatable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

public class MapLoader {

    private final Plugin plugin;
    private final File mapFile;
    private final File lobbyFile;

    private final World world = Bukkit.getWorld("CaptureTheFlag");

    private final Location mapLocation = new Location(world, 0, 50, 0);
    private final Location lobbyLocation = new Location(world, 500, 50, 0);
    private final Location chestLocation = new Location(world, -118, -48, -97);

    public MapLoader(Plugin plugin) {
        this.plugin = plugin;

        mapFile = new File(plugin.getDataFolder() + File.separator + "map.schem");
        lobbyFile = new File(plugin.getDataFolder() + File.separator + "lobby.schem");
    }

    public void load() {
        FAWE.paste(mapFile, mapLocation);
        FAWE.paste(lobbyFile, lobbyLocation);
        loadChest();
    }

    public void loadChest() {
        setChest();
        setChestHologram();
    }

    public void placeFlag(Team team) {
        Location flagLocation = team.getFlagLocation();
        Material material = team.getFlagMaterial();
        BlockFace blockFace = team.getFlagBlockFace();
        Block block = flagLocation.getBlock();
        block.setType(material);
        BlockData blockData = block.getBlockData();
        if (blockData instanceof Rotatable rotatable) {
            rotatable.setRotation(blockFace);
            block.setBlockData(rotatable);
        }
    }

    private void setChest() {
        Location location = chestLocation;
        location.getBlock().setType(Material.CHEST);
        if (location.getBlock().getState() instanceof Chest chest) {
            BlockData data = chest.getBlockData();
            if (data instanceof Rotatable rotatable) {
                rotatable.setRotation(BlockFace.EAST);
                chest.setBlockData(rotatable);
            }
            for (int i = 0; i < 5; i++) {
                ItemStack item = Main.specialService.getRandomSpecial().getItemStack();
                if (item == null) {
                    Bukkit.getLogger().log(Level.INFO, "Special is null at getChest()");
                    continue;
                }
                chest.getBlockInventory().setItem(i, item);
            }
        }
    }

    private void setChestHologram() {
        Hologram hologram = DHAPI.getHologram("special_chest");
        if (hologram != null) {
            DHAPI.removeHologram("special_chest");
            DHAPI.createHologram("special_chest", chestLocation.clone().add(0, 3, 0).toCenterLocation(), List.of(
                    "&a&lTreasure Chest!",
                    "&7Find great items in this chest!"
            ));
            return;
        }
        DHAPI.createHologram("special_chest", chestLocation.clone().add(0, 3, 0).toCenterLocation(), List.of(
                "&a&lTreasure Chest!",
                "&7Find great items in this chest!"));
    }

    public void removeFlag(Team team) {
        Location flagLocation = team.getFlagLocation();
        Block block = flagLocation.getBlock();
        block.setType(Material.AIR);
    }

    public void reloadMap() {
        FAWE.paste(mapFile, mapLocation);
        loadChest();
    }
}
