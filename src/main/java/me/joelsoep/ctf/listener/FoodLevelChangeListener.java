package me.joelsoep.ctf.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodLevelChangeListener implements Listener {

    @EventHandler
    public void onChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }
}
