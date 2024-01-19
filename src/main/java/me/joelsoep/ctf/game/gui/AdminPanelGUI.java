package me.joelsoep.ctf.game.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.task.CountdownTask;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class AdminPanelGUI extends ChestGui implements Listener {

    private final Player player;
    private Game game;
    private PatternPane background;
    private StaticPane forceStart;
    private StaticPane serverRestart;

    public AdminPanelGUI(Player player) {
        super(3, "Admin Panel");
        this.player = player;
        this.game = Main.getGame();

        this.serverRestart = new StaticPane(1, 1, 1, 1);
        this.forceStart = new StaticPane(4, 1, 1, 1);
        this.background = new PatternPane(0, 0, 9, 3, new Pattern(
                "111111111",
                "101101111",
                "111111111"
        ));

        this.background.bindItem('1', new GuiItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)));
        this.addPane(background);
        this.addPane(serverRestart);
        this.addPane(forceStart);
        this.openMenu();
    }

    public void openMenu() {
        if (!player.hasPermission("ctf.admin")) {
            return;
        }

        this.setOnGlobalClick(event -> { event.setCancelled(true); });

        ItemStack startStack = new ItemStack(Material.TRIPWIRE_HOOK);
        ItemMeta startMeta = startStack.getItemMeta();
        startMeta.displayName(MM.deserialize("<light_purple>Forcestart Game"));
        startMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        startMeta.lore(List.of(
                MM.deserialize("<green>Instantly starts the game.")
        ));
        startStack.setItemMeta(startMeta);

        ItemStack serverRestartStack = new ItemStack(Material.NETHER_STAR);
        ItemMeta serverRestartMeta = serverRestartStack.getItemMeta();
        serverRestartMeta.displayName(MM.deserialize("<red>Restart server"));
        serverRestartMeta.lore(List.of(
                MM.deserialize("<gray>Restart the server")
        ));
        serverRestartStack.setItemMeta(serverRestartMeta);

        GuiItem serverRestart = new GuiItem(serverRestartStack, event -> {
            new RestartConfirmGUI(player);
        });

        GuiItem start = new GuiItem(startStack, event -> {
            if (game.getState() != GameState.WAITING) {
                return;
            }
            if (game.getCountdownTask() != null) {
                game.getCountdownTask().cancel();
            }
            game.setStarting(false);
            game.setSpedUp(false);
            game.getAudience().sendMessage(MM.deserialize("<gray>Game has been force-started by an operator!"));
            game.getAudience().playSound(Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 10, 1));

                game.setCountdownTask(new CountdownTask(
                        plugin,
                        MM.deserialize("<#63f02b>Game has started!"),
                        game.getAudience(),
                        5,
                        true,
                        game
                ));
                game.setStarting(true);
        });
        this.serverRestart.addItem(serverRestart, 0, 0);
        this.forceStart.addItem(start, 0, 0);
        this.show(player);
    }
}
