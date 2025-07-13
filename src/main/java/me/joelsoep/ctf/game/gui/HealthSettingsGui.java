package me.joelsoep.ctf.game.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import eu.decentsoftware.holograms.api.utils.scheduler.S;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.setting.GameSetting;
import me.joelsoep.ctf.game.setting.GameSettingsManager;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.util.GuiUtils;
import me.joelsoep.ctf.util.MM;
import me.joelsoep.ctf.util.test.CC;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class HealthSettingsGui extends ChestGui {

    private final CTFPlayer player;

    private final StaticPane half;
    private final StaticPane full;
    private final StaticPane two;

    public HealthSettingsGui(CTFPlayer player) {
        super(3, "Health Settings");

        this.player = player;
        GuiUtils.cancelGlobalClick(this);
        GuiUtils.cancelGlobalDrag(this);
        GuiUtils.setBackground(this, new Pattern(
                "111111111",
                "110101011",
                "111111111"
        ), 3);
        this.half = new StaticPane(2, 1, 1, 1, Pane.Priority.HIGHEST);
        this.full = new StaticPane(4, 1, 1, 1, Pane.Priority.HIGHEST);
        this.two = new StaticPane(6, 1, 1, 1, Pane.Priority.HIGHEST);

        this.half.addItem(getItem(5), 0, 0);
        this.full.addItem(getItem(10), 0, 0);
        this.two.addItem(getItem(20), 0, 0);
        this.addPane(half);
        this.addPane(full);
        this.addPane(two);
        this.show(player.getPlayer());
    }

    public GuiItem getItem(int hearts) {
        GameSettingsManager manager = player.getGame().getGameSettings();

        ItemStack itemStack = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
        meta.setColor(Color.RED);
        meta.displayName(MM.deserialize("<red>%s Hearts".formatted(hearts)));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        List<Component> lore = new ArrayList<>();
        lore.add(MM.deserialize("<gray>Left-click to change"));
        lore.add(MM.deserialize("<gray>%s votes(s)".formatted(manager.getVotesFor(GameSetting.MAX_HEALTH, hearts * 2))));

        @Nullable Object voteValue = manager.getVote(player, GameSetting.MAX_HEALTH);

        if (voteValue != null) {
            if ((int) voteValue == hearts * 2) {
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                lore.add(MM.deserialize(""));
                lore.add(MM.deserialize("<green>You voted for this option!"));
            }
        }

        meta.lore(lore);
        itemStack.setItemMeta(meta);
        itemStack.setData(
                DataComponentTypes.TOOLTIP_DISPLAY,
                TooltipDisplay.tooltipDisplay()
                        .hiddenComponents(Set.of(DataComponentTypes.POTION_CONTENTS, DataComponentTypes.ENCHANTMENTS))
                        .build()
        );

        GuiItem guiItem = new GuiItem(itemStack, action -> {
            if (!action.isLeftClick()) {
                return;
            }

            if (player.hasCooldown()) {
                return;
            }

            if (!player.isPlus()) {
                player.getPlayer().getInventory().close();
                player.sendMessage(MM.deserialize("<red>You need %sPLUS <red>to vote for health settings!".formatted(CC.PLUS)));
                player.sendMessage(MM.getStoreMessage());
                return;
            }

            if (player.getGame().getState() != GameState.WAITING) {
                player.getPlayer().getInventory().close();
                player.sendMessage(MM.deserialize("<red>The game has already started."));
                return;
            }

            if (manager.hasVotedFor(player, GameSetting.MAX_HEALTH, hearts * 2)) {
                manager.vote(player, GameSetting.MAX_HEALTH, null);
            } else {
                manager.vote(player, GameSetting.MAX_HEALTH, hearts * 2);
            }
            this.update();
            player.setCooldown(1000L);
            player.getPlayer().getInventory().close();
        });
        return guiItem;
    }
}
