package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.event.PlayerJoinGameEvent;
import me.joelsoep.ctf.game.task.CountdownTask;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.util.MM;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class PlayerJoinGameListener implements Listener {

    private final Plugin plugin;

    public PlayerJoinGameListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoinGame(PlayerJoinGameEvent event) {
        Game game = event.getGame();
        CTFPlayer player = event.getPlayer();

        game.getAudience().sendMessage(MM.deserialize("""
                    <gray>[</gray><green>+</green><gray>] %s has joined the game! (%s/%s)"""
                .formatted(player.getPlayer().getName(), game.getPlayerCount(), game.getMaxPlayers())));

        if (game.getCountdownTask() != null) {
            game.getCountdownTask().updateAudience(game.getAudience());
        }

        if (game.getPlayerCount() >= game.getMinPlayers() && !game.isStarting()) {
            game.setCountdownTask(new CountdownTask(
                    plugin,
                    MM.deserialize("<#63f02b>Game has started!"),
                    game.getAudience(),
                    45,
                    true,
                    game
            ));
            game.setStarting(true);
        }

        if (game.getPlayerCount() >= game.getMaxPlayers() && game.isStarting() && !game.isSpedUp()) {
            if (game.getCountdownTask() != null && game.getCountdownTask().getSeconds() > 10) {
                game.getCountdownTask().cancel();
            }
            game.setCountdownTask(new CountdownTask(
                    plugin,
                    MM.deserialize("<#63f02b>Game has started!"),
                    game.getAudience(),
                    10,
                    true,
                    game
            ));
            game.setSpedUp(true);
        }
    }
}
