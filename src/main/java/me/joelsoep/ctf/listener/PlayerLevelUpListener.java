package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.event.PlayerLevelUpEvent;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.util.MM;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerLevelUpListener implements Listener {

    @EventHandler
    public void onLevelUp(PlayerLevelUpEvent event) {
        CTFPlayer player = event.getPlayer();
        Integer oldLevel = event.getOldLevel();
        Integer newLevel = event.getNewLevel();

        if (Main.getGame().getState() != GameState.WAITING && Main.getGame().getState() != GameState.ENDED) {
            return;
        }

        player.getBalance().giveCoins((newLevel - oldLevel) * 10);
        player.sendMessage(MM.deserialize("<green>Level up! <yellow>%s".formatted(player.getLevel())));
        player.getPlayer().playerListName(MM.deserialize("<gray>" + player.getPlayer().getName() + " ⭐" + player.getLevel()));
    }
}
