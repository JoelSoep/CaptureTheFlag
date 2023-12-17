package me.joelsoep.ctf.player;

import lombok.Getter;
import net.kyori.adventure.audience.Audience;

import java.util.HashMap;

@Getter
public class PlayerService {

    private static HashMap<String, CTFPlayer> players;
    private static Audience audience;

    public static void addPlayer(CTFPlayer player) {
        players.put(player.getPlayer().getName(), player);
        audience = Audience.audience(players.values());
    }

    public static void removePlayer(CTFPlayer player) {
        players.remove(player.getPlayer().getName());
        audience = Audience.audience(players.values());
    }

    public static HashMap<String, CTFPlayer> getPlayers() {
        return players;
    }
}
