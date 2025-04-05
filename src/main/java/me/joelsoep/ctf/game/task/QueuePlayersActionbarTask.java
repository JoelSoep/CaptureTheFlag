package me.joelsoep.ctf.game.task;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.board.ActionbarPriority;
import org.bukkit.Bukkit;

public class QueuePlayersActionbarTask {
    private int id;

    public QueuePlayersActionbarTask() {
        actionbar();
    }

    private void actionbar() {
        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (Main.getGameQueue().getQueue() == null) {
                    return;
                }

                Main.getGameQueue().getQueue().forEach(player -> {
                    if (player == null) {
                        return;
                    }
                    player.getActionbarHandler().setMessage(ActionbarPriority.LOW, "<gray>Queue position: <yellow>%s<gray>/<yellow>%s".formatted(Main.getGameQueue().getPosition(player), Main.getGameQueue().getQueue().size()));
                });
            }
        }, 0L, 2L * 20L);
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(id);
    }
}
