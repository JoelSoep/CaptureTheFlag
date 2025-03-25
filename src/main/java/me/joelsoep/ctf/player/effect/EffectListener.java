package me.joelsoep.ctf.player.effect;

import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class EffectListener implements Listener {

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        CTFPlayer ctfPlayer = PlayerService.getPlayer(player.getName());
        if (ctfPlayer == null) {
            return;
        }
        ctfPlayer.getEffects().forEach(playerEffect -> {
            if (playerEffect.getRules().contains(EffectRules.CAN_SNEAK)) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        CTFPlayer ctfPlayer = PlayerService.getPlayer(player.getName());
        if (ctfPlayer == null) {
            return;
        }

        ctfPlayer.getEffects().forEach(playerEffect -> {
            if (playerEffect.getRules().contains(EffectRules.CAN_DAMAGE)) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler
    public void onGlide(EntityToggleGlideEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        CTFPlayer ctfPlayer = PlayerService.getPlayer(player.getName());
        if (ctfPlayer == null) {
            return;
        }

        ctfPlayer.getEffects().forEach(playerEffect -> {
            if (playerEffect.getRules().contains(EffectRules.CAN_GLIDE)) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        CTFPlayer ctfPlayer = PlayerService.getPlayer(player.getName());
        if (ctfPlayer == null) {
            return;
        }
        ctfPlayer.getEffects().forEach(playerEffect -> {
            if (playerEffect.getRules().contains(EffectRules.CAN_MOVE_ANY)) {
                event.setCancelled(true);
                return;
            }

            if (playerEffect.getRules().contains(EffectRules.CAN_MOVE_LOCATION) && event.hasChangedPosition()) {
                event.setCancelled(true);
            }
        });
    }
}
