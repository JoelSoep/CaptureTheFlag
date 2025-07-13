package me.joelsoep.ctf.game.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import io.papermc.paper.inventory.tooltip.TooltipContext;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.GuiUtils;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Jukebox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class DesertLobbyJukeboxGui extends ChestGui {

    private final CTFPlayer player;
    private OutlinePane pane;

    public DesertLobbyJukeboxGui(CTFPlayer player) {
        super(5, "Lobby Jukebox");
        this.player = player;

        GuiUtils.cancelGlobalDrag(this);
        GuiUtils.cancelGlobalClick(this);
        GuiUtils.setBackground(this, new Pattern(
                "111111111",
                "100000001",
                "100000001",
                "100000001",
                "111111111"
        ), 5);
        this.pane = new OutlinePane(1, 1,7, 3, Pane.Priority.HIGHEST);
        populatePane();
        this.addPane(pane);
        this.show(player.getPlayer());
    }

    public void populatePane() {
        for (Material disc : Arrays.stream(Material.values()).filter(mat -> mat.toString().contains("MUSIC_DISC")).toList()) {
            ItemStack itemStack = new ItemStack(disc);
            itemStack.lore(List.of(MM.deserialize("<gray>Left-click to choose!")));
            GuiItem item = new GuiItem(itemStack, event -> {
                CTFPlayer player = PlayerService.getPlayer(event.getWhoClicked().getName());

                if (player == null) {
                    return;
                }

                if (player.getGame().getState() != GameState.WAITING) {
                    return;
                }

                Block block = player.getPlayer().getWorld().getBlockAt(new Location(player.getPlayer().getWorld(), 4, 35, 0));
                if (block.getType() != Material.JUKEBOX) {
                    return;
                }

                Jukebox jukebox = (Jukebox) block.getState();

                jukebox.setRecord(itemStack);
                jukebox.startPlaying();
                jukebox.update();
                player.getPlayer().closeInventory();
                TooltipContext context = TooltipContext.create(false, false);
                PlayerService.getAudience().sendMessage(MM.announce("<gray>Now playing <yellow>").append(itemStack.computeTooltipLines(context, player.getPlayer()).get(1)));
            });
            pane.addItem(item);
        }

        ItemStack stopPlaying = new ItemStack(Material.REDSTONE);
        ItemMeta meta = stopPlaying.getItemMeta();
        meta.displayName(MM.deserialize("<red>Stop playing"));
        meta.lore(List.of(MM.deserialize("<gray>Left-click to stop the jukebox")));
        stopPlaying.setItemMeta(meta);
        GuiItem stop = new GuiItem(stopPlaying, action -> {
            CTFPlayer player = PlayerService.getPlayer(action.getWhoClicked().getName());

            if (player == null) {
                return;
            }

            if (player.getGame().getState() != GameState.WAITING) {
                return;
            }

            Block block = player.getPlayer().getWorld().getBlockAt(new Location(player.getPlayer().getWorld(), 4, 35, 0));
            if (block.getType() != Material.JUKEBOX) {
                return;
            }

            Jukebox jukebox = (Jukebox) block.getState();
            jukebox.setRecord(null);
            jukebox.stopPlaying();
            jukebox.update();
            player.getPlayer().closeInventory();
            PlayerService.getAudience().sendMessage(MM.announce("<gray>Stopped playing songs."));
        });
        pane.addItem(stop);
    }
}
