package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.event.FlagCaptureEvent;
import me.joelsoep.ctf.game.kit.BaseKit;
import me.joelsoep.ctf.map.MapLoader;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.game.team.TeamColor;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

public class FlagCaptureListener implements Listener {

    @EventHandler
    public void onCapture(FlagCaptureEvent event) {
        Game game = event.getGame();
        CTFPlayer ctfPlayer = event.getCtfPlayer();
        Player player = ctfPlayer.getPlayer();
        Team team = ctfPlayer.getTeam();
        Team flagOwner = event.getFlagOwner();
        MapLoader mapLoader = event.getMapLoader();

        if (team == null) {
            Bukkit.getLogger().log(Level.SEVERE, player.getName() + "\\'s team is null @ flag capture!");
            return;
        }

        mapLoader.loadChest();
        team.setPoints(team.getPoints() + 1);
        game.getAudience().sendMessage(MM.deserialize("""
                <gray>Team %s<gray>'s flag has been captured by %s<gray>!"""
                .formatted(flagOwner.getTeamColor().getPrefix(), team.getTeamColor().getColor() + player.getName())));
        game.getAudience().playSound(Sound.sound(Key.key("entity.player.levelup"), Sound.Source.MASTER, 1, 1));
        player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 1));
        player.getInventory().setHelmet(BaseKit.getHelmet(ctfPlayer.getTeam().getTeamColor() == TeamColor.BLUE ? Color.BLUE : Color.RED));
        mapLoader.placeFlag(flagOwner);
        player.setGlowing(false);
        flagOwner.getFlag().setThief(null);
    }
}
