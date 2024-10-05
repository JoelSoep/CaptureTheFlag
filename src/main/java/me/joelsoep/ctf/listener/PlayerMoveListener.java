package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.event.FlagCaptureEvent;
import me.joelsoep.ctf.game.event.FlagStealEvent;
import me.joelsoep.ctf.game.event.GameEndEvent;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.map.MapManager;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.Hotbar;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Arrays;
import java.util.Objects;

public class PlayerMoveListener implements Listener {

    public PlayerMoveListener() {
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        CTFPlayer ctfPlayer = PlayerService.getPlayers().get(player.getName());
        Game game = Main.getGame();
        Location location = event.getTo().toCenterLocation();

        if (!event.hasChangedBlock()) {
            return;
        }

        if (game == null) {
            return;
        }

        if (ctfPlayer == null) {
            return;
        }

        if (game.getState() == GameState.WAITING && player.getLocation().getY() < -99) {
            player.teleport(MapManager.getLobby().getSpawnLocation());
            return;
        }

        if (player.getLocation().getY() < game.getMap().getDeathHeight()) {

            if (game.getState() == GameState.ENDED) {
                new Hotbar(player);
                return;
            }

            if (game.getState() != GameState.RUNNING) {
                return;
            }

            DamageSource damageSource = DamageSource.builder(DamageType.FALL).withDamageLocation(player.getLocation()).build();
            PlayerDeathEvent deathEvent = new PlayerDeathEvent(player, damageSource, Arrays.stream(player.getInventory().getContents()).toList(), 0, "");
            Bukkit.getPluginManager().callEvent(deathEvent);
        }

        if (game.getState().equals(GameState.READY)) {
            if (ctfPlayer.getTeam() == null) {
                return;
            }
            event.setCancelled(true);
        }

        if (game.getState().equals(GameState.RUNNING)) {
            Team team = ctfPlayer.getTeam();
            if (team == null) {
                return;
            }

            if (player.getPlayer() == null) {
                return;
            }
            Team blue = game.getBlue();
            Team red = game.getRed();
            Team oppositeTeam = ctfPlayer.getTeam().getTeamColor() == red.getTeamColor() ? blue : red;

            if (team.getFlag().getLocation().getBlock().getLocation().equals(location.getBlock().getLocation()) && oppositeTeam.getFlag().getThief() != null) {
                if (!oppositeTeam.getFlag().getThief().equals(ctfPlayer)) {
                    return;
                }
                FlagCaptureEvent captureEvent = new FlagCaptureEvent(game, ctfPlayer, oppositeTeam);
                Bukkit.getPluginManager().callEvent(captureEvent);
            }

            if (Objects.equals(team.getPoints(), game.getMaxPoints())) {
                LogModule.GAME.log("Calling GameEndEvent as team %s won.".formatted(team.getTeamColor().toString()));
                GameEndEvent endEvent = new GameEndEvent(game, team, oppositeTeam);
                Bukkit.getPluginManager().callEvent(endEvent);
            }

            if (location.getBlock().getLocation().equals(oppositeTeam.getFlag().getLocation().getBlock().getLocation())) {
                if (oppositeTeam.getFlag().getThief() != null) {
                    return;
                }
                FlagStealEvent stealEvent = new FlagStealEvent(game, ctfPlayer, oppositeTeam);
                Bukkit.getPluginManager().callEvent(stealEvent);
            }

        }
    }
}
