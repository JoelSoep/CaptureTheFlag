package me.joelsoep.ctf.game.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.setting.GameSetting;
import me.joelsoep.ctf.game.setting.GameSettingsManager;
import me.joelsoep.ctf.map.MapManager;
import me.joelsoep.ctf.map.type.GameMap;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.util.GuiUtils;
import me.joelsoep.ctf.util.MM;
import me.joelsoep.ctf.util.test.CC;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MapSettingsGui extends ChestGui {
    private final CTFPlayer player;
    private OutlinePane pane;

    public MapSettingsGui(CTFPlayer player) {
        super(3, "Map Settings");
        this.player = player;
        GuiUtils.setBackground(this, new Pattern(
                "111111111",
                "100000001",
                "111111111"
        ), 3);
        GuiUtils.cancelGlobalClick(this);
        GuiUtils.cancelGlobalDrag(this);
        this.pane = new OutlinePane(1, 1, 7, 1, Pane.Priority.HIGHEST);
        this.populatePane();
        this.addPane(pane);
        this.show(player.getPlayer());
    }

    public void populatePane() {
        List<GameMap> maps = MapManager.getGameMaps();
        for (GameMap map : maps) {
            Material material = map.getIcon();
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            List<Component> lore = new ArrayList<>();
            meta.displayName(MM.deserialize("<gray>" + map.getName()));
            lore.add(MM.deserialize("<gray>Left-click to change"));

            GameSettingsManager manager = player.getGame().getGameSettings();
            Object value = manager.getVote(player, GameSetting.MAP);
            if (value != null && manager.hasVotedFor(player, GameSetting.MAP, map)) {
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                lore.add(MM.deserialize(""));
                lore.add(MM.deserialize("<green>You voted for this map!"));
            }
            meta.lore(lore);
            item.setItemMeta(meta);
            GuiItem guiItem = new GuiItem(item, action -> {
                if (!action.isLeftClick()) {
                    return;
                }

                if (player.hasCooldown()) {
                    return;
                }

                if (!player.isPlus()) {
                    player.getPlayer().getInventory().close();
                    player.sendMessage(MM.deserialize("<red>You need %sPLUS <red>to vote for map settings!".formatted(CC.PLUS)));
                    player.sendMessage(MM.getStoreMessage());
                    return;
                }

                if (player.getGame().getState() != GameState.WAITING) {
                    player.getPlayer().getInventory().close();
                    player.sendMessage(MM.deserialize("<red>The game has already started."));
                    return;
                }

                if (manager.hasVotedFor(player, GameSetting.MAP, map)) {
                    manager.vote(player, GameSetting.MAP, null);
                } else {
                    manager.vote(player, GameSetting.MAP, map);
                }

                this.update();
                player.setCooldown(1000L);
                player.getPlayer().getInventory().close();
            });
            pane.addItem(guiItem);
        }
    }
}
