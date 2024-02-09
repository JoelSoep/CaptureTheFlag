package me.joelsoep.ctf.game.task;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.map.MapManager;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.ParticipationState;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.Hotbar;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class ResetGameTask {

    private Game game;
    private Integer id;

    public ResetGameTask(Game game) {
        this.game = game;
        reset();
    }

    public void reset() {
        this.id = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
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

                game.getMap().load();
                game.getPlayers().clear();
                game.getSpectators().clear();
                game.getXPCollector().empty();

                LogModule.CORE.log("Resetting game with UUId " + game.getUuid().toString());
                LogModule.CORE.log("Before assignment - Main.getGame() identity: " + System.identityHashCode(Main.getGame()));
                game.reset();
                LogModule.CORE.log("After assignment - Main.getGame() identity: " + System.identityHashCode(Main.getGame()));
                LogModule.CORE.log("Verifying game UUID @ ResetGameTask: " + game.getUuid().toString());
                LogModule.CORE.log("END OF LOG FILE --- NEW GAME");
                Main.getCTFLogger().resetFileHandler(game.getUuid().toString());

                PlayerService.getPlayers().values().forEach(player -> {
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

                    if (!Main.getQueue().getQueue().contains(player)) {
                        Main.getQueue().addPlayer(player, false);
                        if (game.getPlayers().size() >= game.getMap().getMaxPlayers()) {
                            player.sendMessage(MM.deserialize("""
                                    <gray>Couldn't add you to the game as it is full!
                                    """));
                            return;
                        }
                    }
                });

                PlayerService.getPlayers().values().forEach(player -> {
                    new Hotbar(player.getPlayer());
                });
            }
        }, 400L);
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(id);
    }
}
