package me.joelsoep.ctf.listener;

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
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class PlayerSpawnListener implements Listener {

    @EventHandler
    public void onSpawn(PlayerSpawnLocationEvent event) {
        Game game = Main.getGame();
        Player player = event.getPlayer();

        if (MapManager.getLobby().getSpawnLocation() == null) {
            player.kick(MM.deserialize("""
                    <red>We are preparing Capture the Flag.
                    <red>Please try again later.
                    """));
            return;
        }

        CTFPlayer ctfPlayer = PlayerService.getPlayer(player.getName());
        if (ctfPlayer == null) {
            LogModule.CORE.log("CTFPlayer %s was null while trying to retrieve waitingSystemMessages.".formatted(player.getName()));
            return;
        }

        if (!ctfPlayer.getWaitingSystemMessages().isEmpty()) {
            ctfPlayer.getWaitingSystemMessages().forEach(message -> {
                ctfPlayer.sendMessage(MM.deserialize(message));
            });
        }

        if (game.getState() == GameState.WAITING || game.getState() == GameState.ENDED) {
            event.setSpawnLocation(MapManager.getLobby().getSpawnLocation());
        }

        if (game.getState() == GameState.READY || game.getState() == GameState.RUNNING) {
            event.setSpawnLocation(game.getMap().getCenterLocation());
        }
    }
}


