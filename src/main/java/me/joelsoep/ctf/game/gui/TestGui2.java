package me.joelsoep.ctf.game.gui;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TestGui2 extends ChestGui {
    public TestGui2(Player player) {
        super(3, "test gui 2");
        this.show(player);
    }
}
