package me.joelsoep.ctf.game.task;

import me.joelsoep.ctf.CTFPlayer;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.ParticipationState;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.util.Hotbar;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

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
        Game newGame = new Game();
        this.id = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                // Create a copy of game.getPlayers() to avoid ConcurrentModificationException
                List<CTFPlayer> playersToRemove = new ArrayList<>(game.getPlayers());
                List<CTFPlayer> spectatorsToRemove = new ArrayList<>(game.getSpectators());

                playersToRemove.forEach(player -> {
                    player.getPlayer().teleport(new Location(Bukkit.getWorld("CaptureTheFlag"), 516, 35, 13));
                    game.removePlayer(player);
                    player.reset();
                });

                spectatorsToRemove.forEach(player -> {
                    player.getPlayer().teleport(new Location(Bukkit.getWorld("CaptureTheFlag"), 516, 35, 13));
                    game.removeSpectator(player);
                    player.reset();
                });

                game.getPlayers().clear();
                game.getSpectators().clear();

                Main.playerList.values().forEach(player -> {
                    new Hotbar(player.getPlayer());

                    if (player.getGame() != null) {
                        return;
                    }

                    if (player.getParticipationState() == ParticipationState.UNAVAILABLE) {
                        return;
                    }

                    if (player.getParticipationState() == ParticipationState.SPECTATING) {
                        newGame.addSpectator(player);
                        return;
                    }

                    if (newGame.getPlayers().size() >= newGame.getMaxPlayers()) {
                        return;
                    }
                    newGame.addPlayer(player);
                    player.sendMessage(MM.deserialize("""
                        <green>You have been added to a game with uuid %s.
                        <green>The game will start shortly.
                        """.formatted(newGame.getUuid())));
                });
                newGame.createTeams();

                Main.game = newGame;
                game = newGame;
                Bukkit.getLogger().info("Verifying game UUID @ ResetGameTask: " + game.getUuid().toString());

                if (game.getPlayerCount() >= game.getMinPlayers() && !game.isStarting()) {
                    Main.countdownTask = new CountdownTask(
                            plugin,
                            MM.deserialize("<#63f02b>Game has started!"),
                            game.getAudience(),
                            45,
                            true,
                            game
                    );
                    game.setStarting(true);
                }


            }
        }, 400L);
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(id);
    }
}
