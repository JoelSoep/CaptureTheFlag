package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

public class InventoryMoveItemListener implements Listener {

    @EventHandler
    public void onMove(InventoryMoveItemEvent event) {
        Game game = Main.getGame();

        if (game.getState() == GameState.WAITING || game.getState() == GameState.ENDED) {
            event.setCancelled(true);
        }
    }
}
