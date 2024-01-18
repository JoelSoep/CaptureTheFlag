package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.event.FlagReturnEvent;
import me.joelsoep.ctf.game.team.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FlagReturnListener implements Listener {

    @EventHandler
    public void onReturn(FlagReturnEvent event) {
        Game game = event.getGame();
        Team team = event.getFlagOwner();
        Player player = event.getFlagHolder();

        player.setGlowing(false);
        game.getMap().placeFlag(team);
        team.getFlag().setThief(null);

    }
}
