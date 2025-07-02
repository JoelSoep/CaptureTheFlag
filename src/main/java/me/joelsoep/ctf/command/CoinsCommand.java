package me.joelsoep.ctf.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.MM;
import org.bukkit.entity.Player;

@CommandAlias("coins")
public final class CoinsCommand extends BaseCommand {

    @Default
    @CatchUnknown
    public void coins(Player player) {
        CTFPlayer ctfPlayer = PlayerService.getPlayer(player.getName());
        if (ctfPlayer == null) {
            player.sendMessage("Couldn't fetch profile!");
            return;
        }

        player.sendMessage(MM.announce("<gray>You have <gold>%s <gray>coins.".formatted(ctfPlayer.getData().getCoins())));
    }
}
