package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.board.ScoreboardHandler;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.Hotbar;
import me.joelsoep.ctf.util.MM;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Game game = Main.getGame();
        CTFPlayer ctfPlayer = PlayerService.createPlayer(player);
        event.joinMessage(null);

        if (!Main.getInstance().getServer().getAllowFlight()) {
            if (!(player.hasPermission("ctf.admin"))) {
                return;
            }
            player.sendMessage(MM.deserialize("""
                                        <gray>[<red><bold>CTF-ALERT</bold><gray>]
                                        <red>This server disallows player flight.
                                        <red>It is highly recommended to allow flight, as disabling this could cause issues.
                                        <red>Keeping allow flight disabled is at your own risk.
                    """));
        }

        ScoreboardHandler scoreboardHandler = new ScoreboardHandler(ctfPlayer);
        ctfPlayer.setBoard(scoreboardHandler.getBoard());

        if (game.getState() == GameState.WAITING) {
            ctfPlayer.reset();
            if (game.getPlayerCount() >= game.getMap().getMaxPlayers()) {
                player.sendMessage(MM.deserialize("<gray>You've been added to the queue as the current game is full."));
                Main.getQueue().addPlayer(ctfPlayer);
            } else {
                Main.getQueue().addPlayer(ctfPlayer);
            }
            new Hotbar(ctfPlayer.getPlayer());
        }

        if (game.getState() == GameState.RUNNING || game.getState() == GameState.READY) {
            game.addSpectator(ctfPlayer);
            player.setGameMode(GameMode.SPECTATOR);
            player.getInventory().clear();
            player.sendMessage(MM.deserialize("""
                    <gray>The game has already started.
                    <gray>You have been put in spectator mode.
                    """));
            Main.getQueue().addPlayer(ctfPlayer);
        }

        if (game.getState() == GameState.ENDED) {
            player.sendMessage(MM.deserialize("<red>A game is currently preparing. You will be added shortly."));
            new Hotbar(player);
        }
    }


}