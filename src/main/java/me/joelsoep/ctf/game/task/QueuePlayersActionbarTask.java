package me.joelsoep.ctf.game.task;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.board.ActionbarService;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class QueuePlayersActionbarTask {
    private int id;

    public QueuePlayersActionbarTask() {
        actionbar();
    }

    private void actionbar() {
        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (Main.getQueue().getQueue() == null) {
                    return;
                }

                Main.getQueue().getQueue().forEach(player -> {
                    if (player == null) {
                        return;
                    }
                    player.getActionbarHandler().setMessage(ActionbarService.ActionbarPriority.LOW, "<gray>Queue position: <yellow>%s<gray>/<yellow>%s".formatted(Main.getQueue().getPosition(player), Main.getQueue().getQueue().size()));
                });
            }
        }, 0L, 2L * 20L);
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(id);
    }
}
