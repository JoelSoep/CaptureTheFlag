package me.joelsoep.ctf.game.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import me.joelsoep.ctf.game.kit.BaseKit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class KitSortGUI extends ChestGui {
    private final BaseKit kit;
    private final PatternPane background;
    private StaticPane itemPane;
    private Map<ItemStack, Integer> items;

    public KitSortGUI(BaseKit kit) {
        super(3, "Kit Sorting");
        this.kit = kit;
        this.items = kit.getItems();

        this.background = new PatternPane(0, 0, 9, 3, Pane.Priority.LOW, new Pattern(
                "111111111",
                "000000000",
                "111111111"
        ));

        this.background.bindItem(1, new GuiItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)));
        this.addPane(background);
        this.addPane(itemPane);
    }

    private void openGUI() {
        kit.get
    }
}
