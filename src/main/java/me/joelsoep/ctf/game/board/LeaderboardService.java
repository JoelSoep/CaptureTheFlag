package me.joelsoep.ctf.game.board;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.map.type.LobbyMap;
import me.joelsoep.ctf.player.experience.Level;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardService {

    private ArrayList<Leaderboard> leaderboards;

    public LeaderboardService() {
        leaderboards = new ArrayList<>(List.of(
                new Leaderboard("kill_leaderboard", "&4&l☠ &c&lKills Leaderboard &4&l☠", Main.getMongoManager().getCollection("player"), "player_kills", 0, true, "&4", "&c"),
                new Leaderboard("kd_leaderboard", "&3&l♠ &b&lK/D Leaderboard &3&l♠", Main.getMongoManager().getCollection("player"), "player_kd", (double) 1, false, "&3", "&b"),
                new Leaderboard("capture_leaderboard", "&6&l⚐ &e&lCaptures Leaderboard &6&l⚐", Main.getMongoManager().getCollection("player"), "flag_captures", 0, true, "&6", "&e"),
                new Leaderboard("coin_leaderboard", "&5&l⛃ &d&lCoins Leaderboard &5&l⛃", Main.getMongoManager().getCollection("player"), "coins", 0, true, "&5", "&d"),
                new Leaderboard("experience_leaderboard", "&2&l✪ &aLevel Leaderboard &2&l✪", Main.getMongoManager().getCollection("player"), "experience", 0, true, "&2", "&a", value -> { value.setObject(Level.experienceToLevel(Integer.parseInt(value.getObject().toString())));})
        ));
    }

    public void setLocationsAndCreate(LobbyMap lobbyMap) {
        for (int i = 0; i < leaderboards.size(); i++) {
            Leaderboard leaderboard = leaderboards.get(i);

            if (leaderboard == null) {
                LogModule.UTILITY.warning("Leaderboards are up");
                return;
            }

            Location location = null;
            try {
                location = lobbyMap.getLeaderboardLocations().get(i);
            } catch (Exception ignored) {}

            if (location == null) {
                LogModule.UTILITY.warning("Locations are up.");
                return;
            }
            leaderboard.setLocation(location);
            leaderboard.create();
        }
    }


}
