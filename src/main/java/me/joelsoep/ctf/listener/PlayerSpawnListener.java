package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class PlayerSpawnListener implements Listener {

    @EventHandler
    public void onSpawn(PlayerSpawnLocationEvent event) {
        Game game = Main.game;

        if (Bukkit.getWorld("CaptureTheFlag") == null) {
            event.getPlayer().kick(MM.deserialize("""
                    <red>We are preparing Capture the Flag.
                    <red>Please try again later.
                    """));
            return;
        }

        if (game.getState() == GameState.WAITING || game.getState() == GameState.ENDED) {
            event.setSpawnLocation(new Location(Bukkit.getWorld("CaptureTheFlag"), 516, 35, 13, 138, 0));
        }

        if (game.getState() == GameState.READY || game.getState() == GameState.RUNNING) {
            event.setSpawnLocation(new Location(Bukkit.getWorld("CaptureTheFlag"), -118, -17, -97));
        }
    }
}


