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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

public class AirshipMap extends GameMap {

    private final Location chestLocation = new Location(getWorld(), -14, 24, -2);

    public AirshipMap() {
        super("airship", new File(FileManager.getSchematicFolderPath() + "airship.schem"));
    }

    @Override
    public void load() {
        clearEntities();
        FAWE.paste(this.schematic, new Location(getWorld(), 0, 50, 0));
        setChest();
        createChestHandle();
    }

    @Override
    public void resetFeatures() {
        setChest();
    }

    public void createChestHandle() {
        new Interaction(event -> {
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
                ItemStack item = Main.getSpecialService().getRandomSpecial().getItem();
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
                    "&c&lhas already been opened!",
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
    protected void redFlagLocation() {
        redFlagLocation = new Location(this.getWorld(), -13, 38, 45);
    }

    @Override
    protected void redSpawnLocation() {
        redSpawnLocation = new Location(getWorld(), 34.5, 45.5, 45.5, -180, 0);
    }

    @Override
    protected void redBlockFace() {
        redBlockFace = BlockFace.NORTH;
    }

    @Override
    protected void blueFlagLocation() {
        blueFlagLocation = new Location(getWorld(), -13, 38, -50);
    }

    @Override
    protected void blueSpawnLocation() {
        blueSpawnLocation = new Location(getWorld(), 34.5, 45.5, -49.5);
    }

    @Override
    protected void blueBlockFace() {
        blueBlockFace = BlockFace.SOUTH;
    }

    /**
     *
     * @return The location that is the spectator spawn location as well as the center of the map
     */
    @Override
    protected void centerLocation() {
        centerLocation = new Location(getWorld(), -4, 46, -1);
    }

    @Override
    protected void minPlayers() {
        minPlayers = 6;
    }

    @Override
    protected void maxPlayers() {
        maxPlayers = 14;
    }

    @Override
    protected void worldSize() {
        worldSize = 155;
    }

    @Override
    protected void maxBuildHeight() {
        maxBuildHeight = 123;
    }

    @Override
    protected void deathHeight() {
        deathHeight = -40;
    }
}
