package me.joelsoep.ctf.game.board;

import fr.mrmicky.fastboard.adventure.FastBoard;
import lombok.Getter;
import lombok.Setter;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.util.MM;
import me.joelsoep.ctf.util.Timer;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class ScoreboardHandler implements Listener {

    private final CTFPlayer player;
    private final FastBoard board;
    @Getter
    @Setter
    private String additionalMessage;
    @Getter
    @Setter
    private boolean override = false; // Stop the regular board from playing.
    private int id;

    private static final String ctf = "<b><gradient:#5CFF74:#54F3E6>Capture</gradient><gradient:#54F3E6:#5CFF74> the Flag</gradient></b>";

    public ScoreboardHandler(CTFPlayer player) {
        this.player = player;
        this.board = new FastBoard(player.getPlayer());

        additionalMessage = Main.getInstance().getConfig().getString("additional-message") == null ? "<gray>" : Main.getInstance().getConfig().getString("additional-message");

       this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (board.isDeleted()) {
                    cancel();
                    return;
                }

                if (override) {
                    return;
                }
                update(board);
            }
        }, 0L, 20L);
    }

    public void update(FastBoard board) {
        Game game = Main.getGame();
        if (game.getState() == GameState.RUNNING || game.getState() == GameState.READY) {

            board.updateTitle(MM.deserialize(ctf));
            board.updateLines(
                    message(""),
                    message("<#DCDE50>⌚ <reset>ᴛɪᴍᴇ ʟᴇꜰᴛ: <#DCDE50>" + Timer.formatTimer(
                            game.getState() == GameState.RUNNING
                                    ? game.getGameTime() - System.currentTimeMillis() // Correctly gets the remaining time
                                    : game.getGameTime() // Just format the game time directly if not running
                    )),
                    message(""),
                    message(game.getBlue().getTeamColor().getColor() + "⚐ <reset>ᴄᴀᴘᴛᴜʀᴇѕ: " + game.getBlue().getTeamColor().getColor() + game.getBlue().getPoints() + "<reset>/" + game.getBlue().getTeamColor().getColor() + game.getMaxPoints()),
                    message(game.getRed().getTeamColor().getColor() + "⚐ <reset>ᴄᴀᴘᴛᴜʀᴇѕ: " + game.getRed().getTeamColor().getColor() + game.getRed().getPoints() + "<reset>/" + game.getRed().getTeamColor().getColor() + game.getMaxPoints()),
                    message(""),
                    message("<#861447>☠ <reset>ᴋɪʟʟ ѕᴛʀᴇᴀᴋ: <#861447>" + player.getKillStreak()),
                    message(""),
                    message(additionalMessage)
            );
        }

        if (game.getState() == GameState.WAITING) {
            board.updateTitle(MM.deserialize(ctf));
            board.updateLines(
                    message(""),
                    message("<#DCE71F><bold>+ </bold><reset>ʀᴀɴᴋ: " + (player.isPlus() ? "<b><gradient:#DCE71F:#ECDE19>PLUS</gradient></b>" : "<gray>DEFAULT")),
                    message(""),
                    message("<#FFF800>⛁ <reset>ᴄᴏɪɴѕ: <#FFF800>" + player.getData().getCoins()),
                    message("<#42F348>✪ <reset>ʟᴇᴠᴇʟ: <#42F348>" + player.getLevel()),
                    message(""),
                    message("<#45FFD6>➥ <reset>ᴘʟᴀʏᴇʀѕ: <#45FFD6>" + game.getPlayerCount() + "<reset>/<#45FFD6>" + game.getMap().getMaxPlayers()),
                    message("<#B78EEF>☄ <reset>ᴍᴀᴘ: <#B78EEF>" + WordUtils.capitalize(game.getMap().getName().replace("_", " "))),
                    message(""),
                    message("<#ff5a00>\uD83D\uDD25 <reset>ᴡɪɴ ѕᴛʀᴇᴀᴋ: <#ff5a00>" + player.getData().getWinStreak()),
                    message(""),
                    message(additionalMessage)
            );
        }

        if (game.getState() == GameState.ENDED) {
            int xp = game.getXPCollector().read(player);
            Team winner = game.getWinner();
            board.updateTitle(MM.deserialize(ctf));
            board.updateLines(
                    message(""),
                    message("<#42F348>✪ <reset>ʟᴇᴠᴇʟ: <#42F348>" + player.getLevel() + "<gray> +<#42F348>%s <reset>XP".formatted(xp)),
                    message(""),
                    message("<gray>The game has ended."),
                    message(winner == null ? "<gray>Tied" : "<gray>Team " + winner.getTeamColor().getPrefix() + " <gray>has won!"),
                    message(""),
                    message(additionalMessage)
            );
        }
    }

    public void playDefaultBoard() {

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
