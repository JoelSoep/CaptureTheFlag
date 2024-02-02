package me.joelsoep.ctf.game.task;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.player.CTFPlayer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class QueueCheckGameAvailabilityTask {
    private Game game;
    private int id;

    public QueueCheckGameAvailabilityTask() {
        this.game = Main.getGame();
        check();
    }

    private void check() {
        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (game.getState() != GameState.WAITING) {
                    return;
                }
                if (Main.getQueue().getQueue().isEmpty()) {
                    return;
                }

                if (game.getMap().getMaxPlayers() == game.getPlayers().size()) {
                    return;
                }


                while (!Main.getQueue().getQueue().isEmpty() && game.getMap().getMaxPlayers() != game.getPlayers().size()) {
                    CTFPlayer player = Main.getQueue().getNext();
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
