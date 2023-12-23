package me.joelsoep.ctf.game.board;

import fr.mrmicky.fastboard.adventure.FastBoard;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class ScoreboardHandler implements Listener {

    private final CTFPlayer player;
    private final FastBoard board;
    private final Plugin plugin;
    private int id;

    public ScoreboardHandler(CTFPlayer player, Plugin plugin) {
        this.player = player;
        this.plugin = plugin;
        this.board = new FastBoard(player.getPlayer());

       this.id =  Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
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
        Game game = Main.game;
        if (game.getState() == GameState.RUNNING || game.getState() == GameState.READY) {

            board.updateTitle(MM.deserialize("<green><bold>Capture the Flag"));
            board.updateLines(
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
                    message("<gold>➥ <reset>Players: <gold>" + game.getPlayerCount() + "<reset>/<gold>" + game.getMaxPlayers()),
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
                    message(winner == null ? "Tied" : "<gray>Team " + winner.getTeamColor().getPrefix() + " <gray>has won!"),
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
