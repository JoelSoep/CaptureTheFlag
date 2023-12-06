package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.CTFPlayer;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.event.GameEndEvent;
import me.joelsoep.ctf.game.event.PlayerLeaveGameEvent;
import me.joelsoep.ctf.game.map.MapLoader;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class PlayerLeaveGameListener implements Listener {

    private final Plugin plugin;

    public PlayerLeaveGameListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLeaveGame(PlayerLeaveGameEvent event) {
        Game game = event.getGame();
        CTFPlayer player = event.getPlayer();
        MapLoader mapLoader = new MapLoader(plugin);

        if (game.getPlayers().size() < game.getMinPlayers() && game.isStarting()) {
            if (Main.countdownTask != null) {
                Main.countdownTask.cancel();
                game.setStarting(false);
                game.setSpedUp(false);
                game.getAudience().sendMessage(MM.deserialize("<red>Game preparation has been canceled as there are too few players to start."));
            }
        }
        if (player.getTeam() != null) {
            Team team = player.getTeam();
            Team oppositeTeam = player.getTeam() == game.getRed() ? game.getBlue() : game.getRed();
            team.removePlayer(player);
            player.setTeam(null);

            if (team.getPlayers().size() == 0) {
                GameEndEvent endEvent = new GameEndEvent(game, oppositeTeam, team);
                Bukkit.getPluginManager().callEvent(endEvent);
                return;
            }

            if (oppositeTeam.getFlagThief() != null) {
                if (!(oppositeTeam.getFlagThief().equals(player))) {
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
