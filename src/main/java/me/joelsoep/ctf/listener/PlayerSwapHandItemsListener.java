package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class PlayerSwapHandItemsListener implements Listener {

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        Game game = Main.getGame();
        if (game.getState() == GameState.WAITING) {
            event.setCancelled(true);
        }
    }
}
