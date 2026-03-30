package me.joelsoep.ctf.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import me.joelsoep.ctf.economy.EconomyManager;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.MM;
import org.bukkit.command.CommandSender;
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

    @CommandPermission("ctf.serveradmin")
    @Description("Admin command - Grants a user coins (this amount won't be deducted from your own balance)")
    @Subcommand("grant")
    public void grant(CommandSender sender, OnlinePlayer onlinePlayer, int amount) {
        CTFPlayer ctfPlayer = PlayerService.getPlayer(onlinePlayer.getPlayer());
        if (ctfPlayer == null) {
            sender.sendMessage(MM.deserialize("<red>Could not find player profile."));
            return;
        }

        ctfPlayer.getBalance().giveCoins(amount);
        sender.sendMessage(MM.deserialize("<gray>Gave <yellow>%s <gray>coins to player <yellow>%s<gray>.".formatted(amount, onlinePlayer.getPlayer().getName())));
    }
}
