package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntityListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity entityDamager = event.getDamager();
        Game game = Main.getGame();

        // TODO: Clean up stuff from entitydamagehandler

        if (!(entity instanceof Player player)) {
            return;
        }

        if (game.getState() == GameState.WAITING || game.getState() == GameState.ENDED || game.getState() == GameState.READY) {
            event.setCancelled(true);
            return;
        }

        CTFPlayer ctfPlayer = PlayerService.getPlayers().get(player.getName());

        if (entityDamager instanceof Player damager) {
            CTFPlayer ctfDamager = PlayerService.getPlayers().get(damager.getName());

            if (ctfPlayer.getTeam() == null || ctfDamager.getTeam() == null) {
                event.setCancelled(true);
                return;
            }

            if (ctfPlayer.getTeam().equals(ctfDamager.getTeam())) {
                event.setCancelled(true);
                return;
            }
        }

        if (entityDamager instanceof Arrow arrow) {
            if (!(arrow.getShooter() instanceof Player shooter)) {
                return;
            }

            CTFPlayer ctfShooter = PlayerService.getPlayers().get(shooter.getName());
            if (ctfShooter == null) {
                return;
            }

            if (ctfShooter.getTeam().equals(ctfPlayer.getTeam())) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
