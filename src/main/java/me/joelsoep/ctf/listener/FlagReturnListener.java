package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.event.FlagReturnEvent;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.player.CTFPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FlagReturnListener implements Listener {

    @EventHandler
    public void onReturn(FlagReturnEvent event) {
        Game game = event.getGame();
        Team ownerTeam = event.getFlagOwner();
        CTFPlayer player = event.getFlagHolder();

        player.setGlowing(false);
        game.getMap().placeFlag(ownerTeam);
        ownerTeam.getFlag().setThief(null);

    }
}
