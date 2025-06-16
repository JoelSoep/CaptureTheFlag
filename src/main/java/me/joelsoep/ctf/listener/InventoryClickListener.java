package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Game game = Main.getGame();

        if (event.getInventory().getHolder() instanceof Player player) {
            CTFPlayer ctfPlayer = PlayerService.getPlayer(player.getName());
            if (ctfPlayer == null) {
                return;
            }
            if (ctfPlayer.isElevated()) {
                return;
            }
        }

        if (game.getState() == GameState.WAITING && event.getClick() == ClickType.SWAP_OFFHAND) {
            event.setCancelled(true);
        }

        if (game.getState() != GameState.RUNNING && event.getInventory().getType() == InventoryType.CRAFTING || event.getInventory().getType() == InventoryType.PLAYER) {
            event.setCancelled(true);
        }

        if (event.getCurrentItem() == null) {
            return;
        }

        if (event.getSlotType().equals(InventoryType.SlotType.ARMOR) && !event.getCurrentItem().getType().equals(Material.AIR)) {
            event.setCancelled(true);
        }
    }
}
