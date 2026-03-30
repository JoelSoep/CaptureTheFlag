package me.joelsoep.ctf.game.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import eu.decentsoftware.holograms.api.utils.scheduler.S;
import me.joelsoep.ctf.economy.EconomyManager;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.store.StoreCosmeticItem;
import me.joelsoep.ctf.store.StoreManager;
import me.joelsoep.ctf.util.*;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StoreCosmeticItemVerificationGui extends ChestGui {
    private final CTFPlayer player;
    private final StoreCosmeticItem item;

    private final OutlinePane confirm;
    private final OutlinePane cancel;

    public StoreCosmeticItemVerificationGui(CTFPlayer player, StoreCosmeticItem item) {
        super(3, "Purchase " + StringUtils.capitalize(item.getName().split("\\.")[1]) + "?");
        this.player = player;
        this.item = item;

        GuiUtils.cancelGlobalClick(this);
        GuiUtils.cancelGlobalDrag(this);
        GuiUtils.setBackground(this, new Pattern(
                "000111222",
                "000111222",
                "000111222"
        ), 3);

        this.confirm = new OutlinePane(0, 0, 3, 3, Pane.Priority.HIGHEST);
        this.cancel = new OutlinePane(6, 0, 3, 3, Pane.Priority.HIGHEST);

        for (int i = 0; i < 9; i++) {
            this.confirm.addItem(getConfirmItem());
            this.cancel.addItem(getCancelItem());
        }
        this.addPane(confirm);
        this.addPane(cancel);
        this.show(player.getPlayer());
    }

    public GuiItem getConfirmItem() {
        ItemStack itemStack = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                .setName(MM.deserialize("<green>Confirm purchase"))
                .setLore(List.of(MM.deserialize("<gray>Purchase for %s".formatted(CC.GOLD + CC.COIN + item.getPrice())), MM.deserialize("<gray>Left-click to purchase")))
                .build();

        GuiItem guiItem = new GuiItem(itemStack, action -> {
            if (!action.isLeftClick()) {
                return;
            }

            if (!player.canPurchase(item)) {
                player.sendMessage(MM.announce("<red>You do not have enough coins to purchase this item!"));
                player.sendMessage(MM.getCoinsMessage());
                player.playSound(SoundUtils.getDenySound());
                player.getPlayer().closeInventory();
                return;
            }

            StoreManager.purchase(item, player);
            player.getPlayer().playSound(SoundUtils.getConfirmSound());
            player.getPlayer().getOpenInventory().close();
        });
        return guiItem;
    }

    public GuiItem getCancelItem() {
        ItemStack itemStack = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .setName(MM.deserialize("<red>Cancel purchase"))
                .setLore(MM.deserialize("<gray>Left-click to cancel"))
                .build();

        GuiItem guiItem = new GuiItem(itemStack, action -> {
            player.getPlayer().getInventory().close();
            player.getPlayer().playSound(SoundUtils.getDenySound());
        });
        return guiItem;
    }
}
