package me.joelsoep.ctf.listener;

import fr.mrmicky.fastboard.adventure.FastBoard;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.MM;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CTFPlayer ctfPlayer = PlayerService.getPlayers().get(player.getName());

        if (ctfPlayer == null) {
            return;
        }

        Game game = ctfPlayer.getGame();

        PlayerService.getPlayers().remove(player.getName(), ctfPlayer);
        if (Main.queue.getQueue().contains(ctfPlayer)) {
            Main.queue.removePlayer(ctfPlayer);
        }

        if (game == null) {
            return;
        }

        FastBoard board = ctfPlayer.getBoard();
        ctfPlayer.setBoard(null);
        if (board != null) {
            board.delete();
        }

        event.quitMessage(MM.deserialize("<gray>[<red>-<gray>] <red>%s <gray>left the game.".formatted(player.getName())));
        game.removePlayer(ctfPlayer);
    }
}
