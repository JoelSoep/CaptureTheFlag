package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.map.MapManager;
import me.joelsoep.ctf.util.garbage.EntityDamageHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class PluginEnableListener implements Listener {

    @EventHandler
    public void onEnable(PluginEnableEvent event) {
        Plugin plugin = event.getPlugin();

        if (!(plugin.getName().equals("CapturetheFlag"))) {
            return;
        }
        Main.specialService.loadSpecials("me.joelsoep.ctf.game.special.specials", plugin);
        new MapManager();

    }
}
