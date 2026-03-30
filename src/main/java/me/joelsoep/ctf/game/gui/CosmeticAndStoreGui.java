package me.joelsoep.ctf.game.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.util.GuiUtils;
import me.joelsoep.ctf.util.Hotbar;
import me.joelsoep.ctf.util.ItemBuilder;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CosmeticAndStoreGui extends ChestGui {
    private final CTFPlayer player;

    private StaticPane cosmetics;
    private StaticPane store;

    public CosmeticAndStoreGui(CTFPlayer player) {
        super(3, "Store & Cosmetics");
        this.player = player;
        GuiUtils.cancelGlobalClick(this);
        GuiUtils.cancelGlobalDrag(this);
        GuiUtils.setBackground(this, new Pattern(
                "111111111",
                "111010111",
                "111111111"
        ), 3);

        this.cosmetics = new StaticPane(3, 1, 1, 1, Pane.Priority.HIGHEST);
        this.store = new StaticPane(5, 1, 1, 1, Pane.Priority.HIGHEST);

        this.cosmetics.addItem(getStoreItem(), 0, 0);
        this.store.addItem(getCosmeticsItem(), 0, 0);
        this.addPane(cosmetics);
        this.addPane(store);
        this.show(player.getPlayer());
    }

    public GuiItem getCosmeticsItem() {
        return new GuiItem(
                new ItemBuilder(Material.HORN_CORAL)
                        .setName(MM.deserialize("<red>Cosmetics"))
                        .setLore(MM.deserialize("<gray>Left-click to view"))
                        .build(),
                event ->{
                    new CosmeticsGUI(player.getPlayer());
                }
        );
    }

    public GuiItem getStoreItem() {
        return new GuiItem(
                new ItemBuilder(Material.TOTEM_OF_UNDYING)
                        .setName(MM.deserialize("<green>Store"))
                        .setLore(MM.deserialize("<gray>Left-click to view"))
                        .build(),
                event -> {
                    new CosmeticStoreGUI(player);
                }
        );
    }
}
