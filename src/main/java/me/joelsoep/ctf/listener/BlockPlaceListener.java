package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Game game = Main.game;
        Location blockLocation = event.getBlockPlaced().getLocation();

        if (game.getState() != GameState.RUNNING) {
            if (!player.hasPermission("ctf.admin")) {
                event.setCancelled(true);
            }
        }
        if (game.getState() == GameState.RUNNING) {
            if (blockLocation.getBlockX() == game.getRed().getFlagLocation().getBlockX() && blockLocation.getBlockZ() == game.getRed().getFlagLocation().getBlockZ() || blockLocation.getBlockX() == game.getBlue().getFlagLocation().getBlockX() && blockLocation.getBlockZ() == game.getBlue().getFlagLocation().getBlockZ()) {
                if (event.getBlockPlaced().getLocation().getBlockY() < -32) {
                    event.setCancelled(true);
                    player.sendMessage(MM.deserialize("<red>You cannot place that here!"));
                }
                return;
            }
            if (blockLocation.getBlockX() == game.getRed().getSpawn().getBlockX() && blockLocation.getBlockZ() == game.getRed().getSpawn().getBlockZ() || blockLocation.getBlockX() == game.getBlue().getSpawn().getBlockX() && blockLocation.getBlockZ() == game.getBlue().getSpawn().getBlockZ()) {
                if (blockLocation.getBlockY() < -22 && blockLocation.getBlockY() > -28) {
                    event.setCancelled(true);
                    player.sendMessage(MM.deserialize("<red>You cannot place that here!"));
                }
                return;
            }
                if (event.getItemInHand().getAmount() >= 63) {
                    return;
                }
                event.getItemInHand().setAmount(event.getItemInHand().getAmount() + 1);
        }
    }
}
