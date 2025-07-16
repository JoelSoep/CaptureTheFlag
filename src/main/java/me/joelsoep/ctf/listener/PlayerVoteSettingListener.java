package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.game.event.PlayerVoteSettingEvent;
import me.joelsoep.ctf.map.MapManager;
import me.joelsoep.ctf.map.type.GameMap;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.audience.Audience;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerVoteSettingListener implements Listener {

    @EventHandler
    public void onVote(PlayerVoteSettingEvent event) {
        CTFPlayer player = event.getPlayer();
        Object value = event.getValue();
        Audience audience = player.getGame().getAudience();

        if (value == null) {
            player.sendMessage("<gray>Removed your vote.");
            return;
        }

        String message = switch (event.getSetting()) {
            case MAX_HEALTH -> "<yellow>%s <gray>has voted for <red><b>%s Hearts</b><gray>!".formatted(player.getPlayer().getName(), (int) value);
            case MAP -> "<yellow>%s <gray>has voted for map <blue>%s<gray>!".formatted(player.getPlayer().getName(), MapManager.getGameMap((GameMap) value).getName());
            case CAPTURE_AMOUNT -> "<yellow>%s <gray>has voted for <light_purple>%s Captures<gray>!".formatted(player.getPlayer().getName(), (int) value);
            case TIME -> "<yellow>%s <gray>has voted for <yellow>%s minutes<gray>!".formatted(player.getPlayer().getName(), (Long) value);
        };

        audience.sendMessage(MM.deserialize(message));
    }
}
