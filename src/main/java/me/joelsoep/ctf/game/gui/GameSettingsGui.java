package me.joelsoep.ctf.game.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.util.GuiUtils;
import me.joelsoep.ctf.util.ItemBuilder;
import me.joelsoep.ctf.util.MM;
import me.joelsoep.ctf.util.CC;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.List;
import java.util.Set;

public class GameSettingsGui extends ChestGui {
    private final CTFPlayer player;
    private OutlinePane healthPane;
    private OutlinePane mapPane;
    private OutlinePane capturePane;
    private OutlinePane timePane;

    public GameSettingsGui(CTFPlayer player) {
        super(3, "Game Settings");
        this.player = player;
        this.healthPane = new OutlinePane(1, 1, 1, 1, Pane.Priority.HIGHEST);
        this.mapPane = new OutlinePane(3, 1, 1, 1, Pane.Priority.HIGHEST);
        this.capturePane = new OutlinePane(5, 1, 1, 1, Pane.Priority.HIGHEST);
        this.timePane = new OutlinePane(7, 1, 1, 1, Pane.Priority.HIGHEST);
        GuiUtils.setBackground(this, new Pattern(
                "111111111",
                "101010101", // maybe in case of 4 we do 101010101
                "111111111"
        ), 3);

        if (player.getGame() == null) {
            player.sendMessage("<red>You are not in a game right now!");
            return;
        }
        this.healthPane.addItem(getHealthSettings());
        this.mapPane.addItem(getMapItem());
        this.capturePane.addItem(getCaptureAmountItem());
        this.timePane.addItem(getTimeSettingsItem());
        this.addPane(healthPane);
        this.addPane(mapPane);
        this.addPane(capturePane);
        this.addPane(timePane);
        this.show(player.getPlayer());
    }

    public GuiItem getHealthSettings() {
        ItemStack item = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.setColor(Color.RED);
        meta.displayName(MM.deserialize("<red>Change Health Settings"));
        meta.setHideTooltip(true);
        if (player.isPlus()) {
            meta.lore(List.of(
                    MM.deserialize("<gray>Change the amount of hearts.")
            ));
        } else {
            meta.lore(List.of(
                    MM.deserialize("<red>Buy %s<b>PLUS</b> <red>in order to vote for Health Settings!".formatted(CC.PLUS))
            ));
        }
        item.setItemMeta(meta);
        item.setData(
                DataComponentTypes.TOOLTIP_DISPLAY,
                TooltipDisplay.tooltipDisplay()
                        .hiddenComponents(Set.of(DataComponentTypes.POTION_CONTENTS, DataComponentTypes.ENCHANTMENTS))
                        .build()
        );
        GuiItem guiItem = new GuiItem(item, action -> {
            if (!action.isLeftClick()) {
                return;
            }

            if (!player.isPlus()) {
                player.getPlayer().getInventory().close();
                player.sendMessage(MM.deserialize("<red>You need %s<b>PLUS</b> <red>to vote for health settings!".formatted(CC.PLUS)));
                player.sendMessage(MM.getStoreMessage());
                return;
            }

            if (player.getGame().getState() != GameState.WAITING) {
                player.getPlayer().getInventory().close();
                player.sendMessage(MM.deserialize("<red>The game has already started."));
                return;
            }

           this.update();
           new HealthSettingsGui(player);
        });
        return guiItem;
    }

    public GuiItem getMapItem() {
        ItemStack itemStack = new ItemStack(Material.FILLED_MAP);
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(MM.deserialize("<blue>Map Settings"));
        if (player.isPlus()) {
            meta.lore(List.of(
                    MM.deserialize("<gray>Change the map.")
            ));
        } else {
            meta.lore(List.of(
                    MM.deserialize("<red>Buy %s<b>PLUS</b> <red>in order to vote for Map Settings!".formatted(CC.PLUS))
            ));
        }
        itemStack.setItemMeta(meta);
        GuiItem guiItem = new GuiItem(itemStack, action -> {
            if (!action.isLeftClick()) {
                return;
            }

            if (!player.isPlus()) {
                player.getPlayer().getInventory().close();
                player.sendMessage(MM.deserialize("<red>You need %s<b>PLUS</b> <red>to vote for map settings!".formatted(CC.PLUS)));
                player.sendMessage(MM.getStoreMessage());
                return;
            }

            if (player.getGame().getState() != GameState.WAITING) {
                player.getPlayer().getInventory().close();
                player.sendMessage(MM.deserialize("<red>The game has already started."));
                return;
            }

            this.update();
            new MapSettingsGui(player);
        });
        return guiItem;
    }

    public GuiItem getCaptureAmountItem() {
        ItemStack itemStack = new ItemStack(Material.RED_BANNER);
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(MM.deserialize("<light_purple>Capture Amount Settings"));
        if (player.isPlus()) {
            meta.lore(List.of(
                    MM.deserialize("<gray>Change the amount of captures.")
            ));
        } else {
            meta.lore(List.of(
                    MM.deserialize("<red>Buy %s<b>PLUS</b> <red>in order to vote for Capture Amount Settings!".formatted(CC.PLUS))
            ));
        }
        itemStack.setItemMeta(meta);
        GuiItem guiItem = new GuiItem(itemStack, action -> {
            if (!action.isLeftClick()) {
                return;
            }

            if (!player.isPlus()) {
                player.getPlayer().getInventory().close();
                player.sendMessage(MM.deserialize("<red>You need %s<b>PLUS</b> <red>to vote for Capture Amount settings!".formatted(CC.PLUS)));
                player.sendMessage(MM.getStoreMessage());
                return;
            }

            if (player.getGame().getState() != GameState.WAITING) {
                player.getPlayer().getInventory().close();
                player.sendMessage(MM.deserialize("<red>The game has already started."));
                return;
            }

            this.update();
            new CaptureAmountSettingsGui(player);
        });
        return guiItem;
    }

    public GuiItem getTimeSettingsItem() {
        ItemStack itemStack = new ItemBuilder(Material.CLOCK).build();
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(MM.deserialize("<yellow>Time Settings"));
        if (player.isPlus()) {
            meta.lore(List.of(
                    MM.deserialize("<gray>Change the game time.")
            ));
        } else {
            meta.lore(List.of(
                    MM.deserialize("<red>Buy %s<b>PLUS</b> <red>in order to vote for Time Settings!".formatted(CC.PLUS))
            ));
        }
        itemStack.setItemMeta(meta);
        GuiItem guiItem = new GuiItem(itemStack, action -> {
            if (!action.isLeftClick()) {
                return;
            }

            if (!player.isPlus()) {
                player.getPlayer().getInventory().close();
                player.sendMessage(MM.deserialize("<red>You need %s<b>PLUS</b> <red>to vote for Time settings!".formatted(CC.PLUS)));
                player.sendMessage(MM.getStoreMessage());
                return;
            }

            if (player.getGame().getState() != GameState.WAITING) {
                player.getPlayer().getInventory().close();
                player.sendMessage(MM.deserialize("<red>The game has already started."));
                return;
            }

            this.update();
            new TimeSettingsGui(player);
        });
        return guiItem;
    }
}
