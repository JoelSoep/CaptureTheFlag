package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.file.LocalConfig;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.MM;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class PlayerGameModeChangeListener implements Listener {

    @EventHandler
    public void onChange(PlayerGameModeChangeEvent event) {
        if (event.getNewGameMode() == GameMode.ADVENTURE) {
            return;
        }

        if (event.getCause() != PlayerGameModeChangeEvent.Cause.COMMAND) {
            return;
        }

        CTFPlayer ctfPlayer = PlayerService.getPlayer(event.getPlayer().getName());
        if (ctfPlayer == null) {
            event.getPlayer().sendMessage(MM.deserialize("<red>Couldn't switch gamemode as your profile could not be fetched."));
            return;
        }

        if (ctfPlayer.isElevated()) {
            return;
        }

        if (LocalConfig.FORCE_GAME_MODE) {
            event.setCancelled(true);
            event.cancelMessage(MM.deserialize("Can't switch gamemode because force-gamemode is enabled."));
        }
    }
}
