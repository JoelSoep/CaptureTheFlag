package me.joelsoep.ctf.listener;

import io.papermc.paper.event.player.AsyncPlayerSpawnLocationEvent;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.map.MapManager;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.MM;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerSpawnListener implements Listener {

    @EventHandler
    public void onSpawn(AsyncPlayerSpawnLocationEvent event) {
        Game game = Main.getGame();
        if (MapManager.getLobby().getSpawnLocation() == null) {
            event.getConnection().disconnect(MM.deserialize("""
                    <red>We are preparing Capture the Flag.
                    <red>Please try again later.
                    """));
            return;
        }

        LogModule.CORE.log(MapManager.getLobby().getSpawnLocation().toString());

        if (game.getState() == GameState.WAITING || game.getState() == GameState.ENDED) {
            event.setSpawnLocation(MapManager.getLobby().getSpawnLocation());
        }

        if (game.getState() == GameState.READY || game.getState() == GameState.RUNNING) {
            event.setSpawnLocation(game.getMap().getCenterLocation());
        }
    }
}


