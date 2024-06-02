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
        pane.bindItem('1', new GuiItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)));
        gui.setOnGlobalClick(event -> event.setCancelled(true));
        gui.addPane(pane);
    }
}
