package me.joelsoep.ctf.game.team;

import me.joelsoep.ctf.CTFPlayer;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class TeamService {

    public static void setTeams(Game game) {
        ArrayList<CTFPlayer> players = game.getPlayers();
        Team blue = game.getBlue();
        Team red = game.getRed();
        ArrayList<CTFPlayer> bluePlayers = game.getBlue().getPlayers();
        ArrayList<CTFPlayer> redPlayers = game.getRed().getPlayers();

        Bukkit.getLogger().info("red players size: " + redPlayers.size());
        Bukkit.getLogger().info("blue players size: " + bluePlayers.size());

        Bukkit.getLogger().log(Level.INFO, "Picked players before team randomization (registered in game): ");
        game.getPlayers().forEach(player -> { Bukkit.getLogger().log(Level.INFO, player.getPlayer().getName()); });

        if (players.size() == 0) {
            game.getAudience().sendMessage(MM.deserialize("<red>Couldn't create teams as there are no players to join!"));
            return;
        }

        if (players.size() > game.getMaxPlayers()) {
            List<CTFPlayer> newPlayers = players.subList(0, game.getMaxPlayers() - 1);
            players.clear();
            players.addAll(newPlayers);
            game.setPlayers(players);
            Bukkit.getLogger().log(Level.INFO, "Picked players at team randomization: ");
            game.getPlayers().forEach(player -> { Bukkit.getLogger().log(Level.INFO, player.getPlayer().getName()); });
        }

        for (CTFPlayer player : players) {
            if (bluePlayers.size() <= redPlayers.size()) {
                blue.addPlayer(player);
                player.setTeam(game.getBlue());
            } else {
                red.addPlayer(player);
                player.setTeam(game.getRed());
            }
        }
        Bukkit.getLogger().log(Level.INFO, "Team red: ");
        redPlayers.forEach(player -> { Bukkit.getLogger().log(Level.INFO, player.getPlayer().getName());});
        Bukkit.getLogger().log(Level.INFO, "Team blue: ");
        bluePlayers.forEach(player -> { Bukkit.getLogger().log(Level.INFO, player.getPlayer().getName());});

        game.getPlayers().forEach(player -> {
            Bukkit.getLogger().info("""
                ---- DEBUG LOG FOR PLAYER %s | GAME START ----
                CTFPlayer Team: %s
                CTFPlayer Game: %s
                ---- END DEBUG LOG ---
                """.formatted(player.getPlayer().getName(), player.getTeam().getTeamColor().toString(), player.getGame().getUuid().toString()));
        });
    }
}
