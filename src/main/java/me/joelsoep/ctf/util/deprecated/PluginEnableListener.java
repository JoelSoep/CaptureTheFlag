package me.joelsoep.ctf.util.deprecated;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;

@Deprecated
@ApiStatus.ScheduledForRemoval
public class PluginEnableListener implements Listener {

    @EventHandler
    public void onEnable(PluginEnableEvent event) {
        Plugin plugin = event.getPlugin();

        if (!(plugin.getName().equals("CapturetheFlag"))) {
            return;
        }

    }
}
