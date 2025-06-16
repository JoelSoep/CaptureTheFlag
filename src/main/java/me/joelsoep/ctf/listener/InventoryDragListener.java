package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;

public class InventoryDragListener implements Listener {

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
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

        if (game.getState() == GameState.WAITING || game.getState() == GameState.ENDED) {
            event.setCancelled(true);
        }
    }
}
