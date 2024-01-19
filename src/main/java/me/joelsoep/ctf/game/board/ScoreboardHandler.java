package me.joelsoep.ctf.game.board;

import fr.mrmicky.fastboard.adventure.FastBoard;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.util.MM;
import me.joelsoep.ctf.util.Timer;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class ScoreboardHandler implements Listener {

    private final CTFPlayer player;
    private final FastBoard board;
    private int id;

    public ScoreboardHandler(CTFPlayer player) {
        this.player = player;
        this.board = new FastBoard(player.getPlayer());

       this.id =  Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (board.isDeleted()) {
                    cancel();
                    return;
                }
                update(board);
            }
        }, 0L, 20L);
    }

    public void update(FastBoard board) {
        Game game = Main.getGame();
        if (game.getState() == GameState.RUNNING || game.getState() == GameState.READY) {

            board.updateTitle(MM.deserialize("<green><bold>Capture the Flag"));
            board.updateLines(
                    message(""),
                    message("<gold>⌚ <reset>Time left: <gold>" + (game.getState() == GameState.RUNNING ? Timer.formatTimer(game.getGameTime() - System.currentTimeMillis()) : Timer.formatTimer(game.getGameTime() + 1))),
                    message(""),
                    message(game.getBlue().getTeamColor().getColor() + "⚐ <reset>Captures: " + game.getBlue().getTeamColor().getColor() + game.getBlue().getPoints() + "<reset>/" + game.getBlue().getTeamColor().getColor() + game.getMaxPoints()),
                    message(game.getRed().getTeamColor().getColor() + "⚐ <reset>Captures: " + game.getRed().getTeamColor().getColor() + game.getRed().getPoints() + "<reset>/" + game.getRed().getTeamColor().getColor() + game.getMaxPoints()),
                    message(""),
                    message("<gold>☠ <reset>Killstreak: <gold>" + player.getKillStreak()),
                    message(""),
                    message("<italic><gold>mcmanhunt.com")
            );
        }

        if (game.getState() == GameState.WAITING) {
            board.updateTitle(MM.deserialize("<green><bold>Capture the Flag"));
            board.updateLines(
                    message(""),
                    message("<gold>➥ <reset>Players: <gold>" + game.getPlayerCount() + "<reset>/<gold>" + game.getMap().getMaxPlayers()),
                    message("<gold>☄ <reset>Map: <gold>" + StringUtils.capitalize(game.getMap().getName())),
                    message(""),
                    message("<italic><gold>mcmanhunt.com")
            );
        }

        if (game.getState() == GameState.ENDED) {
            Team winner = game.getWinner();
            board.updateTitle(MM.deserialize("<green><bold>Capture the Flag"));
            board.updateLines(
                    message(""),
                    message("<gray>The game has ended."),
                    message(winner == null ? "<gray>Tied" : "<gray>Team " + winner.getTeamColor().getPrefix() + " <gray>has won!"),
                    message(""),
                    message("<italic><gold>mcmanhunt.com")
            );
        }
    }

    public Component message(String text) {
        return MM.deserialize(text);
    }

    public FastBoard getBoard() {
        return this.board;
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(this.id);
    }

}
