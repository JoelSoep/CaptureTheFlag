package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.event.FlagReturnEvent;
import me.joelsoep.ctf.game.event.GameEndEvent;
import me.joelsoep.ctf.game.event.PlayerLeaveGameEvent;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.util.IntUtils;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerLeaveGameListener implements Listener {

    @EventHandler
    public void onLeaveGame(PlayerLeaveGameEvent event) {
        Game game = event.getGame();
        CTFPlayer player = event.getPlayer();

        if (game.getPlayers().size() < game.getMap().getMinPlayers() && game.isStarting()) {
            if (game.getCountdownTask() != null) {
                game.getCountdownTask().cancel();
                game.setStarting(false);
                game.setSpedUp(false);
                game.getAudience().sendMessage(MM.deserialize("<red>Game preparation has been canceled as there are too few players to start."));
            }
        }

        editWinStreak(game, player);
        if (game.getState() == GameState.WAITING) {
            game.getGameSettings().removeVotesFor(player);
        }
        if (player.getTeam() != null) {
            Team team = player.getTeam();
            Team oppositeTeam = player.getTeam() == game.getRed() ? game.getBlue() : game.getRed();
            team.removePlayer(player);
            player.setTeam(null);

            if (team.getPlayers().isEmpty() && game.getState() != GameState.ENDED) {
                LogModule.GAME.log("Calling GameEndEvent as PlayerLeaveGameEvent was triggered and the team size was 0.");
                GameEndEvent endEvent = new GameEndEvent(game, oppositeTeam, team);
                Bukkit.getPluginManager().callEvent(endEvent);
                return;
            }

            if (oppositeTeam.getFlag().getThief() != null) {
                if (!(oppositeTeam.getFlag().getThief().equals(player))) {
                    return;
                }
                FlagReturnEvent returnEvent = new FlagReturnEvent(game, oppositeTeam, player);
                Bukkit.getPluginManager().callEvent(returnEvent);
                game.getAudience().sendMessage(
                        MM.deserialize(
                                "<gray>Team %s's flag has been returned."
                                        .formatted(oppositeTeam.getTeamColor().getPrefix()))
                );
            }
        }
    }

    public void editWinStreak(Game game, CTFPlayer player) {
        if (game.getState() == GameState.RUNNING) {
            // if the difference in size between teams is more than 20%, the player can leave without penalty.
            if (!(IntUtils.isWithinPercentage(game.getRed().getPlayers().size(), game.getBlue().getPlayers().size(), 20))) {
                return;
            }
            player.getData().setWinStreak(0);
            LogModule.GAME.log("Win streak of %s has been reset because they left the game while it was running.".formatted(player.getPlayer().getName()));
        }
    }
}
