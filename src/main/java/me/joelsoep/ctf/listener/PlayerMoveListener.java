package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.CTFPlayer;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.event.FlagCaptureEvent;
import me.joelsoep.ctf.game.event.FlagStealEvent;
import me.joelsoep.ctf.game.event.GameEndEvent;
import me.joelsoep.ctf.game.kit.BaseKit;
import me.joelsoep.ctf.game.kit.KitService;
import me.joelsoep.ctf.game.map.MapLoader;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.game.team.TeamColor;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class PlayerMoveListener implements Listener {

    private final Plugin plugin;

    public PlayerMoveListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        CTFPlayer ctfPlayer = Main.playerList.get(player.getName());
        MapLoader mapLoader = new MapLoader(plugin);
        Game game = Main.game;
        Location location = event.getTo().toCenterLocation();

        if (!event.hasChangedBlock()) {
            return;
        }

        if (ctfPlayer == null) {
            return;
        }

        if (player.getLocation().getY() < -99) {
            player.teleport(new Location(Bukkit.getWorld("CaptureTheFlag"), -118, -17, -97));
            PlayerDeathEvent deathEvent = new PlayerDeathEvent(player, Arrays.stream(player.getInventory().getContents()).toList(), 0, "");
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

            if (team.getFlagLocation().getBlock().getLocation().equals(location.getBlock().getLocation()) && oppositeTeam.getFlagThief() != null) {
                if (!oppositeTeam.getFlagThief().equals(ctfPlayer)) {
                    return;
                }
                FlagCaptureEvent captureEvent = new FlagCaptureEvent(game, ctfPlayer, oppositeTeam, mapLoader);
                Bukkit.getPluginManager().callEvent(captureEvent);
            }

            if (Objects.equals(team.getPoints(), game.getMaxPoints())) {
                GameEndEvent endEvent = new GameEndEvent(game, team, oppositeTeam);
                Bukkit.getPluginManager().callEvent(endEvent);
            }

            if (location.getBlock().getLocation().equals(oppositeTeam.getFlagLocation().getBlock().getLocation())) {
                if (oppositeTeam.getFlagThief() != null) {
                    return;
                }
                FlagStealEvent stealEvent = new FlagStealEvent(game, ctfPlayer, oppositeTeam, mapLoader);
                Bukkit.getPluginManager().callEvent(stealEvent);
            }

        }
    }
}
