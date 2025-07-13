package me.joelsoep.ctf.game.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import eu.decentsoftware.holograms.api.utils.scheduler.S;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.util.GuiUtils;
import me.joelsoep.ctf.util.ItemBuilder;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class PlusMenuGui extends ChestGui {
    private final CTFPlayer player;

    private StaticPane settings;
    // can be opened for non plus if they want to view, but further sub menus not
    public PlusMenuGui(CTFPlayer player) {
        super(3, "Plus Menu");
        this.player = player;
        GuiUtils.cancelGlobalDrag(this);
        GuiUtils.cancelGlobalClick(this);
        GuiUtils.setBackground(this, new Pattern(
                "111111111",
                "111010111",
                "111111111"
        ), 3);

        this.settings = new StaticPane(3, 1, 1, 1, Pane.Priority.HIGHEST);
        this.settings.addItem(getSettingsItem(), 0, 0);
        this.addPane(settings);
        this.show(player.getPlayer());
    }

    public GuiItem getSettingsItem() {
        return new GuiItem(new ItemBuilder(Material.BREWING_STAND)
                .setName(MM.deserialize("<gold>Game Settings"))
                .setLore(MM.deserialize("<gray>Alter Game Settings"), MM.deserialize("<gray>Left-click to view"))
                .build(),
                 event -> {
            new GameSettingsGui(player);
        });
    }
}
