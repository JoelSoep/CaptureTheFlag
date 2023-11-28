package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class EntityPickupItemListener implements Listener {

    @EventHandler
    public void onPickUp(EntityPickupItemEvent event) {
        Game game = Main.game;

        if (game.getState() != GameState.WAITING) {
            event.setCancelled(true);
        } else {
            if (!(event.getEntity() instanceof Player player)) {
                event.setCancelled(true);
                return;
            }
            if (!player.hasPermission("ctf.admin")) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
