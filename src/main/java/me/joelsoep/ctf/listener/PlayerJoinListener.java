package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.map.MapManager;
import me.joelsoep.ctf.map.type.GameMap;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.Hotbar;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import java.util.Comparator;
import java.util.Optional;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Game game = Main.getGame();
        CTFPlayer ctfPlayer = PlayerService.createPlayer(player);
        event.joinMessage(null);

   //     Plugin plugin = Bukkit.getPluginManager().getPlugin("mcmanhunt-nametags");
   //     if (plugin == null) {
   //         LogModule.CORE.warning("Could not disable chat listener from MCManhunt-NameTags as the plugin is null.");
   //         return;
    //    }

    //    for (RegisteredListener listener : event.getHandlers().getRegisteredListeners()) {
    //        if (listener.getPlugin().getName().equals("MCManhunt-Nametags")) {
     //           LogModule.CORE.log("unregistered mcmanhunt-nametags");
      //          event.getHandlers().unregister(plugin);
      //      }
    //    }

        if (!player.hasPermission("ctf.admin") && !player.isOp() && !Main.getSettings().isOpen() && !PlayerService.isAllowedToJoin(player.getUniqueId())) {
            player.kick(MM.deserialize("""
                <gray>[<red><bold>Capture the Flag</bold><gray>]
                <gray>This server is currently closed.
                <gray>Please try again later.
                """));
            return;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (!ctfPlayer.getWaitingSystemMessages().isEmpty()) {

                    ctfPlayer.getWaitingSystemMessages().forEach(message -> {
                        ctfPlayer.sendMessage(MM.deserialize(message));
                    });
                    ctfPlayer.getWaitingSystemMessages().clear();
                }
            }
        }, 20L);

        if (!Main.getInstance().getServer().getAllowFlight()) {
            if (player.hasPermission("ctf.admin")) {
                player.sendMessage(MM.deserialize("""
                        <gray>[<red><bold>CTF-ALERT</bold><gray>]
                        <red>This server disallows player flight.
                        <red>It is highly recommended to allow flight, as disabling this could cause issues.
                        <red>Keeping allow flight disabled is at your own risk.
                        """));
            }
        }

        if (game.getState() == GameState.WAITING) {
            ctfPlayer.reset();

            // scales the map if there is one available with higher player capacity when the old capacity has been reached

            LogModule.GAME.log("pcount: %s, mplayers: %s".formatted( game.getPlayerCount(), game.getMap().getMaxPlayers()));
            if (game.getPlayerCount() >= game.getMap().getMaxPlayers()) {
                Optional<GameMap> replacement = MapManager.getGameMaps().stream()
                        .filter(gameMap -> gameMap.getMaxPlayers() > game.getPlayerCount())
                        .min(Comparator.comparing(GameMap::getMaxPlayers));

                LogModule.GAME.log("CHECKING FOR OTHER MAP");

                if (replacement.isPresent()) {
                    LogModule.MAP.log("Scaling map to map %s as max players for map %s was reached.".formatted(replacement.get().getName(), game.getMap().getName()));
                    MapManager.registerMapToGame(replacement.get(), game);
                    LogModule.MAP.log("Successfully scaled to map %s.".formatted(replacement.get().getName()));
                    if (game.getCountdownTask() != null) {
                        game.getCountdownTask().cancel();
                    }
                }
            }

            if (game.getPlayerCount() >= game.getMap().getMaxPlayers()) {
                player.sendMessage(MM.deserialize("<gray>You've been added to the queue as the current game is full."));
                Main.getGameQueue().addPlayer(ctfPlayer);
            } else {
                Main.getGameQueue().addPlayer(ctfPlayer);
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
            Main.getGameQueue().addPlayer(ctfPlayer);
        }

        if (game.getState() == GameState.ENDED) {
            player.sendMessage(MM.deserialize("<red>A game is currently preparing. You will be added shortly."));
            new Hotbar(player);
        }
    }


}