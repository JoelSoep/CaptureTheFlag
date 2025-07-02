package me.joelsoep.ctf.game.gui;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import me.joelsoep.ctf.player.CTFPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TestGui extends ChestGui {
    private final Player player;
    public TestGui(Player player) {
        super(3, "test gui");
        this.player = player;
        closeLogic();
        this.show(player);
    }

    public void closeLogic() {
        this.setOnClose(event -> {
            System.out.println("hello");
            new TestGui2(player);
        });
    }
}
