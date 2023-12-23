package me.joelsoep.ctf.game.task;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class QueuePlayersActionbarTask {
    private final Plugin plugin;
    private int id;

    public QueuePlayersActionbarTask(Plugin plugin) {
        this.plugin = plugin;
        actionbar();
    }

    private void actionbar() {
        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (Main.queue.getQueue() == null) {
                    return;
                }

                Main.queue.getQueue().forEach(player -> {
                    if (player == null) {
                        return;
                    }
                    player.sendActionBar(MM.deserialize("<gray>Queue position: <yellow>%s<gray>/<yellow>%s".formatted(Main.queue.getPosition(player), Main.queue.getQueue().size())));
                });
            }
        }, 0L, 5L * 20L);
    }

    public void cancel(int id) {
        Bukkit.getScheduler().cancelTask(id);
    }
}
