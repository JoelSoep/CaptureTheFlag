package me.joelsoep.ctf.game.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.setting.GameSetting;
import me.joelsoep.ctf.game.setting.GameSettingsManager;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.util.GuiUtils;
import me.joelsoep.ctf.util.MM;
import me.joelsoep.ctf.util.CC;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CaptureAmountSettingsGui extends ChestGui {
    private final CTFPlayer player;

    private StaticPane three;
    private StaticPane five;
    private StaticPane seven;

    public CaptureAmountSettingsGui(CTFPlayer player) {
        super(3, "Capture Amount Settings");
        this.player = player;

        GuiUtils.cancelGlobalClick(this);
        GuiUtils.cancelGlobalDrag(this);
        GuiUtils.setBackground(this, new Pattern(
                "111111111",
                "110101011",
                "111111111"
        ), 3);
        this.three = new StaticPane(2, 1, 1, 1, Pane.Priority.HIGHEST);
        this.five = new StaticPane(4, 1, 1, 1, Pane.Priority.HIGHEST);
        this.seven = new StaticPane(6, 1, 1, 1, Pane.Priority.HIGHEST);

        this.three.addItem(getItem(3), 0, 0);
        this.five.addItem(getItem(5), 0, 0);
        this.seven.addItem(getItem(7), 0, 0);
        this.addPane(three);
        this.addPane(five);
        this.addPane(seven);
        this.show(player.getPlayer());
    }


    public GuiItem getItem(int captures) {
        GameSettingsManager manager = player.getGame().getGameSettings();

        ItemStack itemStack = new ItemStack(Material.LIGHT_GRAY_BANNER);
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(MM.deserialize("<light_purple>%s Captures".formatted(captures)));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        List<Component> lore = new ArrayList<>();
        lore.add(MM.deserialize("<gray>Left-click to change"));
        lore.add(MM.deserialize("<gray>%s votes(s)".formatted(manager.getVotesFor(GameSetting.CAPTURE_AMOUNT, captures))));

        @Nullable Object voteValue = manager.getVote(player, GameSetting.CAPTURE_AMOUNT);

        if (voteValue != null) {
            if ((int) voteValue == captures) {
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                lore.add(MM.deserialize(""));
                lore.add(MM.deserialize("<green>You voted for this option!"));
            }
        }

        meta.lore(lore);
        itemStack.setItemMeta(meta);

        GuiItem guiItem = new GuiItem(itemStack, action -> {
            if (!action.isLeftClick()) {
                return;
            }

            if (player.hasCooldown()) {
                return;
            }

            if (!player.isPlus()) {
                player.getPlayer().getInventory().close();
                player.sendMessage(MM.deserialize("<red>You need %sPLUS <red>to vote for capture amount settings!".formatted(CC.PLUS)));
                player.sendMessage(MM.getStoreMessage());
                return;
            }

            if (player.getGame().getState() != GameState.WAITING) {
                player.getPlayer().getInventory().close();
                player.sendMessage(MM.deserialize("<red>The game has already started."));
                return;
            }

            if (manager.hasVotedFor(player, GameSetting.CAPTURE_AMOUNT, captures)) {
                manager.vote(player, GameSetting.CAPTURE_AMOUNT, null);
            } else {
                manager.vote(player, GameSetting.CAPTURE_AMOUNT, captures);
            }
            this.update();
            player.setCooldown(1000L);
            player.getPlayer().getInventory().close();
        });
        return guiItem;
    }

}
