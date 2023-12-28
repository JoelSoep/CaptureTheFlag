package me.joelsoep.ctf.game.task;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.map.MapManager;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.ParticipationState;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.Hotbar;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class ResetGameTask {

    private final Plugin plugin;
    private Game game;
    private Integer id;

    public ResetGameTask(Plugin plugin, Game game) {
        this.plugin = plugin;
        this.game = game;
        reset();
    }

    public void reset() {
        this.id = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                // Create a copy of game.getPlayers() to avoid ConcurrentModificationException
                List<CTFPlayer> playersToRemove = new ArrayList<>(game.getPlayers());
                List<CTFPlayer> spectatorsToRemove = new ArrayList<>(game.getSpectators());

                playersToRemove.forEach(player -> {
                    player.getPlayer().teleport(MapManager.getLobby().getSpawnLocation());
                    game.removePlayer(player);
                    player.reset();
                });

                spectatorsToRemove.forEach(player -> {
                    player.getPlayer().teleport(MapManager.getLobby().getSpawnLocation());
                    game.removeSpectator(player);
                    player.reset();
                });

                game.getPlayers().clear();
                game.getSpectators().clear();

                Bukkit.getLogger().info("Before assignment - Main.game identity: " + System.identityHashCode(Main.game));
                game.reset();
                Bukkit.getLogger().info("After assignment - Main.game identity: " + System.identityHashCode(Main.game));
                Bukkit.getLogger().info("Verifying game UUID @ ResetGameTask: " + game.getUuid().toString());

                PlayerService.getPlayers().values().forEach(player -> {
                    new Hotbar(player.getPlayer());

                    if (player.getGame() != null) {
                        return;
                    }

                    if (player.getParticipationState() == ParticipationState.UNAVAILABLE) {
                        return;
                    }

                    if (player.getParticipationState() == ParticipationState.SPECTATING) {
                        game.addSpectator(player);
                        return;
                    }

                    Main.queue.addPlayer(player, false);
                    if (game.getPlayers().size() >= game.getMaxPlayers()) {
                        player.sendMessage(MM.deserialize("""
                                <gray>Couldn't add you to the game as it is full!
                                """));
                        return;
                    }
                });
            }
        }, 400L);
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(id);
    }
}
