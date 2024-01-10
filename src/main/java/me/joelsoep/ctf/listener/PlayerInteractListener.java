package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.gui.AdminPanelGUI;
import me.joelsoep.ctf.game.gui.KitSelectionGUI;
import me.joelsoep.ctf.game.gui.TeamSelectionGUI;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.map.MapManager;
import me.joelsoep.ctf.map.type.game.AirshipMap;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.Hotbar;
import me.joelsoep.ctf.util.MM;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

/**
 * TODO: Make it so this class is not depending on the game state, but rather if a player is in a game or not.
 */

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Game game = Main.game;

        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || game.getState() == GameState.WAITING && event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        Player player = event.getPlayer();
        CTFPlayer ctfPlayer = PlayerService.getPlayers().get(player.getName());
        Material material = event.getMaterial();
        ItemStack item = event.getItem();

        if (item == null) {
            return;
        }

        if (ctfPlayer == null) {
            return;
        }

        if (ctfPlayer.hasCooldown()) {
            return;
        }

        ctfPlayer.setCooldown(500L);

        if (material == Material.BOOK) {
            new KitSelectionGUI(player, Main.kitService);
            return;
        }

        if (material == Material.GRAY_BANNER) {
            if (game.getState() != GameState.WAITING) {
                player.sendMessage(MM.deserialize("<red>Can't open the team selector right now, please try once the current game is over."));
                return;
            }
            new TeamSelectionGUI(player);
            return;
        }

        if (material == Material.LIME_CONCRETE) {
            if (Main.queue.getQueue().contains(ctfPlayer)) {
                Main.queue.removePlayer(ctfPlayer);
            }

            if (Main.game.getPlayers().contains(ctfPlayer) && game.getState() == GameState.WAITING) {
                Main.game.removePlayer(ctfPlayer);
            }
            Hotbar.Static.giveQueueBlock(player);
            return;
        }

        if (material == Material.RED_CONCRETE) {
            if (Main.queue.getQueue().contains(ctfPlayer)) {
                return;
            }

            if (Main.game.getPlayers().contains(ctfPlayer)) {
                return;
            }
            Main.queue.addPlayer(ctfPlayer);
            Hotbar.Static.giveQueueBlock(player);
            return;
        }

        if (event.getMaterial() == Material.COMMAND_BLOCK) {
            if (!player.hasPermission("ctf.admin")) {
                player.sendMessage(MM.deserialize("<red>You do not have permission to use this!"));
                return;
            }
            if (!item.hasItemFlag(ItemFlag.HIDE_DYE)) {
                return;
            }
            new AdminPanelGUI(player);
        }
    }
}
