package me.joelsoep.ctf.map.type.game;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.file.FileManager;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.map.type.GameMap;
import me.joelsoep.ctf.util.FAWE;
import me.joelsoep.ctf.util.MM;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

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
        world.setSpawnLocation(getCenterLocation());
        createChestHandle();
    }

    @Override
    public void resetFeatures() {
        setChest();
    }

    public void createChestHandle() {
        Interaction interaction = new Interaction(event -> {
            Game game = Main.getGame();
            if (game.getState() != GameState.RUNNING) {
                event.setCancelled(true);
                return;
            }
            Player player = event.getPlayer();
            if (event.getClickedBlock() == null) {
                return;
            }

            if (!(event.getClickedBlock().getState() instanceof Chest chest)) {
                return;
            }

            if (player.getGameMode() == GameMode.SPECTATOR) {
                event.setCancelled(true);
                return;
            }
            if (player.getGameMode() != GameMode.ADVENTURE) {
                return;
            }
            if (!(game.getMap() instanceof AirshipMap airshipMap)) {
                return;
            }

            if (!(chest.getLocation().getWorld().getName().equals(airshipMap.getWorld().getName()))) {
                return;
            }
            airshipMap.openChest(player);
            event.setCancelled(true);
        });
    }

    public void openChest(Player player) {
        Chest chest = getChest();
        if (chest == null) {
            return;
        }

        if (Main.getGame().getState() != GameState.RUNNING) {
            return;
        }

        if (chest.isOpen()) {
            return;
        }

        chest.getBlockInventory().forEach(itemStack -> {
            if (itemStack == null) {
                return;
            }
            player.getInventory().addItem(itemStack);
        });
        chest.getBlockInventory().clear();
        setChestOpened(true);
        player.sendMessage(MM.deserialize("<green>Claimed chest!<br><gray>Check you inventory for more details."));
        LogModule.MAP.log("Player %s claimed the treasure chest in airship-map!".formatted(player.getName()));
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
                ItemStack item = Main.getSpecialService().getRandomSpecial().getItemStack();
                if (item == null) {
                    LogModule.MAP.log("Special is null at getChest()");
                    continue;
                }
                chest.getBlockInventory().setItem(i, item);
                setChestOpened(false);
                setClosedChestHologram();
            }
        }
    }

    private @Nullable Chest getChest() {
        Block block = chestLocation.getBlock();
        if (!(block.getState() instanceof Chest chest)) {
            return null;
        }
        return chest;
    }

    private void setChestOpened(boolean value) {
        Chest chest = getChest();
        if (chest == null) {
            LogModule.MAP.warning("CHEST IS NULL! @ AirshipMap");
            return;
        }
        if (value) {
            chest.open();
            setOpenChestHologram();
            return;
        }
        chest.close();
    }

    private void setClosedChestHologram() {
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

    private void setOpenChestHologram() {
        Hologram hologram = DHAPI.getHologram("special_chest");
        if (hologram != null) {
            DHAPI.removeHologram("special_chest");
            DHAPI.createHologram("special_chest", chestLocation.clone().add(0, 3, 0).toCenterLocation(), List.of(
                    "&c&lThis treasure chest",
                    "&c&lalready been opened!",
                    "&7Please try again later."
            ));
            return;
        }
        DHAPI.createHologram("special_chest", chestLocation.clone().add(0, 3, 0).toCenterLocation(), List.of(
                "&c&lThis treasure chest",
                "&c&lalready been opened!",
                "&7Please try again later."
        ));
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

    /**
     *
     * @return the location that is the spectator spawn location as well as the center of the map
     */
    @Override
    public @NotNull Location getCenterLocation() {
        return new Location(getWorld(), -104, -22, -95);
    }

    @Override
    public @NotNull Integer getMaxPlayers() {
        return 14;
    }

    @Override
    public @NotNull Integer getWorldSize() {
        return 155;
    }

    @Override
    public @NotNull Integer getMaxBuildHeight() {
        return 25;
    }
}
