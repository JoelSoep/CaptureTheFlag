package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class InventoryDropListener implements Listener {

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {

            CTFPlayer ctfPlayer = PlayerService.getPlayer(event.getPlayer().getName());
            if (ctfPlayer == null) {
                return;
            }
            if (ctfPlayer.isElevated()) {
                return;
            }


        event.setCancelled(true);
    }
}
