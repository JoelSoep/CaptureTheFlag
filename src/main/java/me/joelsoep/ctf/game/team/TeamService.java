package me.joelsoep.ctf.game.team;

import lombok.Getter;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class TeamService {

    @Getter
    private static final List<Team> teams = new ArrayList<>();

    public static Team createTeam(TeamColor color, Location spawn, Location flagLocation) {
        Team team = new Team(color, spawn, flagLocation);
        getTeams().add(team);
        return team;
    }

    public static void setTeams(Game game) {
        ArrayList<CTFPlayer> players = game.getPlayers();
        Team blue = game.getBlue();
        Team red = game.getRed();
        ArrayList<CTFPlayer> bluePlayers = game.getBlue().getPlayers();
        ArrayList<CTFPlayer> redPlayers = game.getRed().getPlayers();

        LogModule.GAME.log("red players size: " + redPlayers.size());
        LogModule.GAME.log("blue players size: " + bluePlayers.size());

        LogModule.GAME.log("Picked players before team randomization (registered in game): ");
        game.getPlayers().forEach(player -> { LogModule.GAME.log(player.getPlayer().getName()); });

        if (players.size() == 0) {
            game.getAudience().sendMessage(MM.deserialize("<red>Couldn't create teams as there are no players to join!"));
            LogModule.GAME.log("No teams created --- players.size() == 0");
            return;
        }

        if (players.size() > game.getMap().getMaxPlayers()) {
            List<CTFPlayer> newPlayers = players.subList(0, game.getMap().getMaxPlayers() - 1);
            players.clear();
            players.addAll(newPlayers);
            game.setPlayers(players);
            LogModule.GAME.log("Picked players at team randomization: ");
            game.getPlayers().forEach(player -> { LogModule.GAME.log(player.getPlayer().getName()); });
        }

        for (CTFPlayer player : players) {
            if (player.getTeam() != null) {
                continue;
            }
            if (bluePlayers.size() <= redPlayers.size()) {
                blue.addPlayer(player);
            } else {
                red.addPlayer(player);
            }
        }
        LogModule.GAME.log("Team red: ");
        redPlayers.forEach(player -> { LogModule.GAME.log(player.getPlayer().getName());});
        LogModule.GAME.log("Team blue: ");
        bluePlayers.forEach(player -> { LogModule.GAME.log(player.getPlayer().getName());});

        game.getPlayers().forEach(player -> {
            LogModule.GAME.log("""
                ---- DEBUG LOG FOR PLAYER %s | GAME START ----
                CTFPlayer Team: %s
                CTFPlayer Game: %s
                ---- END DEBUG LOG ---
                """.formatted(player.getPlayer().getName(), player.getTeam().getTeamColor().toString(), player.getGame().getUuid().toString()));
        });
    }
}
