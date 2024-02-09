package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.file.LocalConfig;
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

        if (LocalConfig.FORCE_GAME_MODE) {
            event.setCancelled(true);
            event.cancelMessage(null);
        }
    }
}
