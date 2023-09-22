package me.joelsoep.ctf.listener;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class AdvancementListener implements Listener {

    @EventHandler
    public void onAdvance(PlayerAdvancementCriterionGrantEvent event) {
        if (event.getAdvancement().getRoot().getDisplay() == null) {
            return;
        }
        if (!event.getAdvancement().getRoot().getDisplay().title().equals(Component.text("Capture the Flag"))) {
            event.setCancelled(true);
            return;
        }


    }
}
