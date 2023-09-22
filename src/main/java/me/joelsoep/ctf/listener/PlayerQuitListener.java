package me.joelsoep.ctf.listener;

import fr.mrmicky.fastboard.adventure.FastBoard;
import me.joelsoep.ctf.CTFPlayer;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.event.GameEndEvent;
import me.joelsoep.ctf.game.map.MapLoader;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class PlayerQuitListener implements Listener {

    private final Plugin plugin;

    public PlayerQuitListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CTFPlayer ctfPlayer = Main.playerList.get(player.getName());
        MapLoader mapLoader = new MapLoader(plugin);

        if (ctfPlayer == null) {
            return;
        }

        Game game = ctfPlayer.getGame();

        Main.playerList.remove(player.getName(), ctfPlayer);

        if (game == null) {
            return;
        }

        FastBoard board = ctfPlayer.getBoard();
        ctfPlayer.setBoard(null);
        if (board != null) {
            board.delete();
        }

        event.quitMessage(MM.deserialize("<gray>[<red>-<gray>] <red>%s <gray>left the game.".formatted(player.getName())));
        game.removePlayer(ctfPlayer);
        if (game.getPlayers().size() < game.getMinPlayers() && game.isStarting()) {
            if (Main.countdownTask != null) {
                Main.countdownTask.cancel();
                game.setStarting(false);
                game.setSpedUp(false);
                game.getAudience().sendMessage(MM.deserialize("<red>Game preparation has been canceled as there are too few players to start."));
            }
        }
        if (ctfPlayer.getTeam() != null) {
            Team team = ctfPlayer.getTeam();
            Team oppositeTeam = ctfPlayer.getTeam() == game.getRed() ? game.getBlue() : game.getRed();
            team.getPlayers().remove(ctfPlayer);

            if (team.getPlayers().size() == 0) {
                GameEndEvent endEvent = new GameEndEvent(game, oppositeTeam, team);
                Bukkit.getPluginManager().callEvent(endEvent);
                return;
            }

            if (oppositeTeam.getFlagThief() != null) {
                if (!(oppositeTeam.getFlagThief().equals(ctfPlayer))) {
                    return;
                }
                    oppositeTeam.setFlagThief(null);
                    mapLoader.placeFlag(oppositeTeam);
                    game.getAudience().sendMessage(
                            MM.deserialize(
                                    "<gray>Team %s's flag has been returned."
                                            .formatted(oppositeTeam.getTeamColor().getPrefix()))
                    );
            }
        }
    }
}
