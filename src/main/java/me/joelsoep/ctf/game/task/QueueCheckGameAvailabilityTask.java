package me.joelsoep.ctf.game.task;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.player.CTFPlayer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class QueueCheckGameAvailabilityTask {
    private final Plugin plugin;
    private Game game;
    private int id;

    public QueueCheckGameAvailabilityTask(Plugin plugin) {
        this.plugin = plugin;
        this.game = Main.game;
        check();
    }

    private void check() {
        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (game.getState() != GameState.WAITING) {
                    return;
                }
                if (Main.queue.getQueue().isEmpty()) {
                    return;
                }

                if (game.getMap().getMaxPlayers() == game.getPlayers().size()) {
                    return;
                }


                while (!Main.queue.getQueue().isEmpty() && game.getMap().getMaxPlayers() != game.getPlayers().size()) {
                    CTFPlayer player = Main.queue.getNext();
                    if (player == null) {
                        continue;
                    }

                    game.addPlayer(player);
                }
            }
        }, 0L, 20L);
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(id);
    }
}
