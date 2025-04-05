package me.joelsoep.ctf.game.task;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.board.ActionbarHandler;
import me.joelsoep.ctf.game.board.ActionbarPriority;
import me.joelsoep.ctf.player.CTFPlayer;
import org.bukkit.Bukkit;

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
                if (Main.getGameQueue().getQueue().isEmpty()) {
                    return;
                }

                if (game.getMap().getMaxPlayers() == game.getPlayers().size()) {
                    return;
                }


                while (!Main.getGameQueue().getQueue().isEmpty() && game.getMap().getMaxPlayers() != game.getPlayers().size()) {
                    CTFPlayer player = Main.getGameQueue().getNext();
                    if (player == null) {
                        continue;
                    }

                    game.addPlayer(player);
                    player.getActionbarHandler().removeMessage(ActionbarPriority.LOW);
                }
            }
        }, 0L, 20L);
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(id);
    }
}
