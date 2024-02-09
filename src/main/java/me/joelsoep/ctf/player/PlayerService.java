package me.joelsoep.ctf.player;

import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.HashMap;

@Getter
public class PlayerService {

    private static final HashMap<String, CTFPlayer> players = new HashMap<>();
    private static Audience audience = Audience.audience(players.values());

    public static CTFPlayer createPlayer(Player player) {
        CTFPlayer ctfPlayer = new CTFPlayer(player);
        addPlayer(ctfPlayer);
        return ctfPlayer;
    }

    public static @Nullable CTFPlayer getPlayer(String name) {
        return players.get(name);
    }

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

    public static Audience getAudience() {
        return audience;
    }
}
