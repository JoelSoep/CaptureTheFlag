package me.joelsoep.ctf.map.type.game;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.file.FileManager;
import me.joelsoep.ctf.map.type.GameMap;
import me.joelsoep.ctf.util.FAWE;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rotatable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

@SuppressWarnings("unused")
public class AirshipMap extends GameMap {

    private final Location chestLocation = new Location(getWorld(), -118, -48, -97);

    public AirshipMap() {
        super("airship", new File(FileManager.getSchematicFolderPath() + "airship.schem"));
    }

    @Override
    public void load() {
        FAWE.paste(this.schematic, new Location(getWorld(), 0, 50, 0));
        setChest();
        setChestHologram();
    }

    @Override
    public void resetFeatures() {
        setChest();
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

    @Override
    public @NotNull Location getRedFlagLocation() {
        return new Location(this.getWorld(), -117, -34, -50);
    }

    @Override
    public @NotNull Location getRedSpawnLocation() {
        return new Location(getWorld(), -70, -26.5, -49.5, -180, 0);
    }

    @Override
    public @NotNull BlockFace getRedBlockFace() {
        return BlockFace.NORTH;
    }

    @Override
    public @NotNull Location getBlueFlagLocation() {
        return new Location(getWorld(), -117, -34, -145);
    }

    @Override
    public @NotNull Location getBlueSpawnLocation() {
        return new Location(getWorld(), -70, -26.5, -144.5);
    }

    @Override
    public @NotNull BlockFace getBlueBlockFace() {
        return BlockFace.SOUTH;
    }

    @Override
    public @NotNull Location getSpectatorSpawnLocation() {
        return new Location(getWorld(), -104, -22, -95);
    }

    @Override
    public @NotNull Integer getMaxPlayers() {
        return 20;
    }
}
