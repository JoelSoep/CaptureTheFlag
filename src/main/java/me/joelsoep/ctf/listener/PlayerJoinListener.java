package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.board.ScoreboardHandler;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.util.Hotbar;
import me.joelsoep.ctf.util.MM;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class PlayerJoinListener implements Listener {

    private final Plugin plugin;

    public PlayerJoinListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Game game = Main.game;
        CTFPlayer ctfPlayer = new CTFPlayer(player);
        event.joinMessage(null);

        if (game.getState() == GameState.WAITING) {
            ctfPlayer.reset();
            // Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "advancement revoke " + player.getName() + " everything");
            if (game.getPlayerCount() >= game.getMaxPlayers()) {
                player.sendMessage(MM.deserialize("<gray>yellow>You've been added to the queue as the current game is full."));
                Main.queue.addPlayer(ctfPlayer);
            } else {
                Main.queue.addPlayer(ctfPlayer);
            }
            new Hotbar(ctfPlayer.getPlayer());
            ScoreboardHandler scoreboardHandler = new ScoreboardHandler(ctfPlayer, plugin);
            ctfPlayer.setBoard(scoreboardHandler.getBoard());
        }

        if (game.getState() == GameState.RUNNING || game.getState() == GameState.READY) {
            game.addSpectator(ctfPlayer);
            player.setGameMode(GameMode.SPECTATOR);
            player.getInventory().clear();
            player.sendMessage(MM.deserialize("""
                    <gray>The game has already started.
                    <gray>You have been put in spectator mode.
                    """));
            Main.queue.addPlayer(ctfPlayer);
        }

        if (game.getState() == GameState.ENDED) {
            player.sendMessage(MM.deserialize("<red>A game is currently preparing. You will be added shortly."));
            new Hotbar(player);
            ScoreboardHandler scoreboardHandler = new ScoreboardHandler(ctfPlayer, plugin);
            ctfPlayer.setBoard(scoreboardHandler.getBoard());
        }
    }


}