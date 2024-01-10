package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.logger.LogModule;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;

public class InventoryMoveItemListener implements Listener {

    @EventHandler
    public void onMove(InventoryMoveItemEvent event) {
        Game game = Main.game;

        if (game.getState() == GameState.WAITING || game.getState() == GameState.ENDED) {
            event.setCancelled(true);
        }
    }
}
