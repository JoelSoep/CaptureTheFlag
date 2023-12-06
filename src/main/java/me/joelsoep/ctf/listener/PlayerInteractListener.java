package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.gui.AdminPanelGUI;
import me.joelsoep.ctf.game.gui.KitSelectionGUI;
import me.joelsoep.ctf.util.garbage.PlayerParticipationGUI;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Game game = Main.game;
        if (!(game.getState() == GameState.WAITING && event.getAction() == Action.RIGHT_CLICK_AIR || game.getState() == GameState.WAITING && event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        Player player = event.getPlayer();
        Material material = event.getMaterial();
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        if (material == Material.BOOK) {
            new KitSelectionGUI(player, Main.kitService);
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
