package me.joelsoep.ctf.game.gui.admin;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.cosmetic.type.CosmeticType;
import me.joelsoep.ctf.util.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CosmeticListGUI extends ChestGui {
    public CosmeticListGUI(Player player, CosmeticType type) {
        super(3, "Cosmetics Panel");

        OutlinePane pane = new OutlinePane(9, 3);
        Main.getCosmeticsManager().getCosmetics(type).forEach(cosmetic -> {
            ItemStack item = new ItemBuilder(cosmetic.getLogo())
                    .setName(cosmetic.getDisplayName())
                    .setLore(cosmetic.getLore())
                    .build();
            GuiItem guiItem = new GuiItem(item);
            pane.addItem(guiItem);
        });
        this.addPane(pane);
        this.show(player);
    }
}
