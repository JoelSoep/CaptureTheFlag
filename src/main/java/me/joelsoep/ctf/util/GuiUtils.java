package me.joelsoep.ctf.util;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GuiUtils {

    public static void setBackground(ChestGui gui, Pattern pattern, int height) {
        PatternPane pane = new PatternPane(0, 0, 9, height, Pane.Priority.LOWEST, pattern);
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        item.editMeta(meta -> meta.setHideTooltip(true));
        pane.bindItem('1', new GuiItem(item));
        gui.setOnGlobalClick(event -> event.setCancelled(true));
        gui.addPane(pane);
    }

    public static void cancelGlobalClick(ChestGui gui) {
        gui.setOnGlobalClick(action -> action.setCancelled(true));
    }

    public static void cancelGlobalDrag(ChestGui gui) {
        gui.setOnGlobalDrag(action -> action.setCancelled(true));
    }

    public static GuiItem getComingSoon() {
        return new GuiItem(new ItemBuilder(Material.BARRIER)
                .setName("<red>Coming soon")
                .build());
    }
}
