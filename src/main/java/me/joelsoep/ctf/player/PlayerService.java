package me.joelsoep.ctf.player;

import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@Getter
public class PlayerService {

    @Getter
    private static final HashMap<String, CTFPlayer> players = new HashMap<>();
    @Getter
    private static final ArrayList<UUID> whitelist = new ArrayList<>();
    @Getter
    private static Audience audience = Audience.audience(players.values());

    public static CTFPlayer createPlayer(Player player) {
        CTFPlayer ctfPlayer = new CTFPlayer(player);
        addPlayer(ctfPlayer);
        return ctfPlayer;
    }

    public static @Nullable CTFPlayer getPlayer(String name) {
        return players.get(name);
    }

    public static @Nullable CTFPlayer getPlayer(Player player) {
        return players.get(player.getName());
    }

    public static @Nullable CTFPlayer getPlayer(UUID uuid) {
        return players.values().stream()
                .filter(p -> p.getUUID().equals(uuid))
                .findFirst()
                .orElse(null);
    }

    public static void addPlayer(CTFPlayer player) {
        players.put(player.getPlayer().getName(), player);
        audience = Audience.audience(players.values());
    }

    public static void removePlayer(CTFPlayer player) {
        players.remove(player.getPlayer().getName());
        audience = Audience.audience(players.values());
    }

    public static boolean isAllowedToJoin(UUID uuid) {
        return whitelist.contains(uuid);
    }
}
