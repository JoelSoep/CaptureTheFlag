package me.joelsoep.ctf.game.team;

import me.joelsoep.ctf.CTFPlayer;
import me.joelsoep.ctf.game.Game;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class TeamService {

    public static void setTeams(Game game) {
        ArrayList<CTFPlayer> players = game.getPlayers();
        ArrayList<CTFPlayer> bluePlayers = game.getBlue().getPlayers();
        ArrayList<CTFPlayer> redPlayers = game.getRed().getPlayers();

        Bukkit.getLogger().log(Level.INFO, "Picked players before team randomization (registered in game): ");
        game.getPlayers().forEach(player -> { Bukkit.getLogger().log(Level.INFO, player.getPlayer().getName()); });

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
                bluePlayers.add(player);
                player.setTeam(game.getBlue());
            } else {
                redPlayers.add(player);
                player.setTeam(game.getRed());
            }
        }
        Bukkit.getLogger().log(Level.INFO, "Team red: ");
        redPlayers.forEach(player -> { Bukkit.getLogger().log(Level.INFO, player.getPlayer().getName());});
        Bukkit.getLogger().log(Level.INFO, "Team blue: ");
        bluePlayers.forEach(player -> { Bukkit.getLogger().log(Level.INFO, player.getPlayer().getName());});
    }
}
