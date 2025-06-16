package me.joelsoep.ctf.game.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.util.GuiUtils;
import me.joelsoep.ctf.util.ItemBuilder;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class CosmeticStoreGUI extends ChestGui {
    private final CTFPlayer player;
    private final StaticPane itemPane;

    public CosmeticStoreGUI(CTFPlayer player) {
        super(3, "Cosmetics Store");
        GuiUtils.setBackground(this, new Pattern(
                "111111111",
                "110101011",
                "111111111"
        ), 3);

        this.player = player;
        this.itemPane = new StaticPane(2, 1, 5, 1, Pane.Priority.HIGHEST);
        this.setOnGlobalClick(event -> { event.setCancelled(true); });
        this.addPane(itemPane);
        setItems();
        this.show(player.getPlayer());
    }

    private void setItems() {
        GuiItem deathMessage = new GuiItem(new ItemBuilder(Material.SKELETON_SKULL)
                .build(), (action -> {
                    if (!action.isLeftClick()) {
                        return;
                    }
                    new DeathMessageCosmeticStoreGUI(player);
        }), Main.getInstance());
        this.itemPane.addItem(deathMessage, 0, 0);

        // TODO: add other types
        this.itemPane.addItem(GuiUtils.getComingSoon(), 2, 0);
        this.itemPane.addItem(GuiUtils.getComingSoon(), 4, 0);
    }
}
