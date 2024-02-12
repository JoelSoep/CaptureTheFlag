package me.joelsoep.ctf.game.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import me.joelsoep.ctf.util.ItemBuilder;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CosmeticsGUI extends ChestGui {
    private final Player player;
    private PatternPane background;
    private OutlinePane cosmetics;
    private List<GuiItem> items;

    public CosmeticsGUI(Player player) {
        super(3, "Cosmetics");

        this.player = player;
        setBackground();
        addToList();
        setCosmetics();
        this.show(player);
    }

    private void setCosmetics() {
        AtomicInteger unused = new AtomicInteger(5);
        this.cosmetics = new OutlinePane(2, 1, 5, 1, Pane.Priority.HIGHEST);
        this.items.forEach(item -> {
            this.cosmetics.addItem(item);
            unused.getAndDecrement();
        });

        for (int i = 0; i < unused.get(); i++) {
            this.cosmetics.addItem(getComingSoon());
        }
        this.addPane(this.cosmetics);
    }

    private void addToList() {
        this.items = List.of(
                getDeathMessageItem()
        );
    }

    private GuiItem getDeathMessageItem() {
        return new GuiItem(new ItemBuilder(Material.PAPER)
                .setName(MM.deserialize("<light_purple>Death Messages"))
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                .build(), event -> {
            player.sendMessage(MM.deserialize("WIP"));
        });
    }

    private GuiItem getComingSoon() {
        return new GuiItem(new ItemBuilder(Material.BARRIER)
                .setName("<red>Coming soon")
                .build());
    }

    private void setBackground() {
        this.background = new PatternPane(0, 0, 9, 3, Pane.Priority.LOWEST, new Pattern(
                "111111111",
                "110000011",
                "111111111"
        ));
        this.background.bindItem('1', new GuiItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)));
        this.setOnGlobalClick(event -> event.setCancelled(true));
        this.addPane(background);
    }
}
