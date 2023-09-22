package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.advancement.AdvancementService;
import me.joelsoep.ctf.util.EntityDamageHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class PluginEnableListener implements Listener {

    @EventHandler
    public void onEnable(PluginEnableEvent event) {
        Plugin plugin = event.getPlugin();

        if (!(plugin.getName().equals("CapturetheFlag"))) {
            return;
        }
        Main.entityDamageHandler = new EntityDamageHandler();
        Main.specialService.loadSpecials("me.joelsoep.ctf.game.special.specials", plugin);
    }
}
