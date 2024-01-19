package me.joelsoep.ctf.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupArrowEvent;

public class PlayerPickupArrowListener implements Listener {

    @EventHandler
    public void onPickup(PlayerPickupArrowEvent event) {
        event.setCancelled(true);
    }
}
