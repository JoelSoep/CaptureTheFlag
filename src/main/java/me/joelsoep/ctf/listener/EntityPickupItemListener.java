package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class EntityPickupItemListener implements Listener {

    @EventHandler
    public void onPickUp(EntityPickupItemEvent event) {
        Game game = Main.getGame();

        if (event.getEntity() instanceof Player player) {
            CTFPlayer ctfPlayer = PlayerService.getPlayer(player.getName());
            if (ctfPlayer == null) {
                return;
            }
            if (ctfPlayer.isElevated()) {
                return;
            }
        }

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
