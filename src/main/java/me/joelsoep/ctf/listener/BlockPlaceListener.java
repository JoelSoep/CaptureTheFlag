package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
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
        Game game = Main.getGame();
        Location blockLocation = event.getBlockPlaced().getLocation();

        if (game.getState() != GameState.RUNNING) {
            CTFPlayer ctfPlayer = PlayerService.getPlayer(player.getName());
            if (ctfPlayer == null) {
                player.sendMessage(MM.deserialize("<red>Couldn't place block because your profile could not be fetched."));
                event.setCancelled(true);
                return;
            }

            if (ctfPlayer.isElevated()) {
                event.setCancelled(true);
                return;
            }
        }
        if (game.getState() == GameState.RUNNING) {
            if (!(game.getMap().isLocationInMapSize(event.getBlockPlaced().getLocation()))) {
                event.setCancelled(true);
                player.sendMessage(MM.deserialize("<red>You can't place blocks beyond the map!"));
                return;
            }

            if (blockLocation.getBlockX() == game.getRed().getFlag().getLocation().getBlockX() && blockLocation.getBlockZ() == game.getRed().getFlag().getLocation().getBlockZ() || blockLocation.getBlockX() == game.getBlue().getFlag().getLocation().getBlockX() && blockLocation.getBlockZ() == game.getBlue().getFlag().getLocation().getBlockZ()) {
                Team team;
                if (blockLocation.getBlockX() == game.getRed().getFlag().getLocation().getBlockX() && blockLocation.getBlockZ() == game.getRed().getFlag().getLocation().getBlockZ()) {
                    team = game.getRed();
                } else {
                    team = game.getBlue();
                }

                if (blockLocation.getBlockY() == team.getFlag().getLocation().getBlockY() || blockLocation.getBlockY() == team.getFlag().getLocation().clone().add(0, 1, 0).getBlockY()) {
                    event.setCancelled(true);
                    player.sendMessage(MM.deserialize("<red>You cannot place that here!"));
                }
            }
            if (blockLocation.getBlockX() == game.getRed().getSpawn().getBlockX() && blockLocation.getBlockZ() == game.getRed().getSpawn().getBlockZ() || blockLocation.getBlockX() == game.getBlue().getSpawn().getBlockX() && blockLocation.getBlockZ() == game.getBlue().getSpawn().getBlockZ()) {
                Team team;
                if (blockLocation.getBlockX() == game.getRed().getSpawn().getBlockX() && blockLocation.getBlockZ() == game.getRed().getSpawn().getBlockZ()) {
                    team = game.getRed();
                } else {
                    team = game.getBlue();
                }
                if (blockLocation.getBlockY() > team.getSpawn().clone().subtract(0, 2,0).getBlockY() && blockLocation.getBlockY() < team.getSpawn().clone().add(0, 3, 0).getBlockY()) {
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
