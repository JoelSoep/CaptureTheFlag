package me.joelsoep.ctf.game.gui.admin;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.special.SpecialService;
import me.joelsoep.ctf.player.CTFPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SpecialsListGUI extends ChestGui {
    public SpecialsListGUI(Player player) {
        super(3, "Specials Panel");

        OutlinePane pane = new OutlinePane(9, 3);
        Main.getSpecialService().getAllSpecialsIncludingUnavailable().forEach(special -> {
            ItemStack item = special.getItem();
            GuiItem guiItem = new GuiItem(item);
            pane.addItem(guiItem);
        });
        this.addPane(pane);
        this.show(player);
    }
}
