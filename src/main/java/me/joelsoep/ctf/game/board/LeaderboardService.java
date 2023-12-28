package me.joelsoep.ctf.game.board;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.map.type.LobbyMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardService {

    private ArrayList<Leaderboard> leaderboards;
    private Game game = Main.game;

    public LeaderboardService() {
        leaderboards = new ArrayList<>(List.of(
                new Leaderboard("kill_leaderboard", "&4&l☠ &c&lKills Leaderboard &4&l☠", Main.mongoManager.getDatabase().getCollection("player"), "player_kills", 0, true, "&4", "&c"),
                new Leaderboard("kd_leaderboard", "&3&l♠ &b&lK/D Leaderboard &3&l♠", Main.mongoManager.getDatabase().getCollection("player"), "player_kd", (double) 1, false, "&3", "&b")
        ));
    }

    public void setLocationsAndCreate(LobbyMap lobbyMap) {
        for (int i = 0; i < leaderboards.size(); i++) {
            Leaderboard leaderboard = leaderboards.get(i);

            if (leaderboard == null) {
                Bukkit.getLogger().warning("Leaderboards are up");
                return;
            }
            Location location = lobbyMap.getLeaderboardLocations().get(i);

            if (location == null) {
                Bukkit.getLogger().warning("Locations are up.");
                return;
            }
            leaderboard.setLocation(location);
            leaderboard.create();
        }
    }


}
