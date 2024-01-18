package me.joelsoep.ctf.game.task;

import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.event.GameEndEvent;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.logger.LogModule;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class GameTimerTask {
    private final Plugin plugin;
    private final Game game;
    private int id;

    public GameTimerTask(Plugin plugin, Game game) {
        this.plugin = plugin;
        this.game = game;
        start();
    }

    private void start() {
        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (game.getState() == GameState.ENDED) {
                    cancel();
                    return;
                }

                if (game.getGameTime() < System.currentTimeMillis()) {
                    LogModule.CORE.log("Game ended due to time being up");

                    Team winner = null;
                    Team loser = null;

                    if (game.getRed().getPoints() > game.getBlue().getPoints()) {
                        winner = game.getRed();
                        loser = game.getBlue();
                    }

                    if (game.getBlue().getPoints() > game.getRed().getPoints()) {
                        winner = game.getBlue();
                        loser = game.getRed();
                    }
                    GameEndEvent gameEndEvent = new GameEndEvent(game, winner, loser);
                    Bukkit.getPluginManager().callEvent(gameEndEvent);
                    cancel();
                }
                }
        }, 20L, 20L);
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(id);
    }
}
