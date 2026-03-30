package me.joelsoep.ctf.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.economy.Transaction;
import me.joelsoep.ctf.economy.TransactionManager;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.OfflineCTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.store.StoreItem;
import me.joelsoep.ctf.store.StoreManager;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.UUID;

@CommandAlias("cosmetic-admin")
@CommandPermission("ctf.serveradmin")
public class CosmeticAdminCommand extends BaseCommand {

    @Subcommand("give-cosmetic")
    public void giveCosmetic(CommandSender sender, String receiverUUID, @Single String storeItem, String reason) {
        StoreItem item = StoreManager.getItem(storeItem);
        if (item == null) {
            sender.sendMessage(MM.deserialize("<red>This cosmetic doesn't exist."));
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                Transaction transaction = TransactionManager.createTransaction(UUID.fromString(receiverUUID), item, reason, true);
                sender.sendMessage(MM.deserialize("created transaction with id %s".formatted(transaction.getUuid().toString())));
            }
        });
    }
}
