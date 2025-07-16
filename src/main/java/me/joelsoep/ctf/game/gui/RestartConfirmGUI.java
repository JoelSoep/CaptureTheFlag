package me.joelsoep.ctf.game.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.GuiUtils;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class RestartConfirmGUI extends ChestGui {
    private final Player player;
    private final StaticPane confirmButtonPane = new StaticPane(4, 1, 1, 1);

    public RestartConfirmGUI(Player player) {
        super(3, "Restart server?");
        this.player = player;
        GuiUtils.setBackground(this, new Pattern(
                "111111111",
                "111101111",
                "111111111"
        ), 3);
        confirmButtonPane.addItem(getConfirmItem(), 0, 0);
        this.setOnGlobalClick(event -> { event.setCancelled(true); });
        this.addPane(confirmButtonPane);
        this.show(player);
    }

    public GuiItem getConfirmItem() {
        return new GuiItem(
                getItemStack(), event -> {
                    LogModule.CORE.severe("Admin " + player.getName() + " initiated a server restart!");
                    PlayerService.getAudience().sendMessage(MM.deserialize("<gray>[<red><bold>ALERT</bold><gray>] <red>The server will be restarting in <yellow>15 <red>seconds."));
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            Main.getInstance().getServer().restart();
                        }
                    }, 300L);
                }
        );
    }

    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(Material.REDSTONE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(MM.deserialize("<red>Restart Server"));
        itemMeta.lore(List.of(
                MM.deserialize("<gray>Announces a server restart."),
                MM.deserialize("<gray>Only for emergency use."),
                MM.deserialize("<gray>The server restarts in 15 seconds.")
        ));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
