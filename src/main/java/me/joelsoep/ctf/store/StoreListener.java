package me.joelsoep.ctf.store;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.cosmetic.Cosmetic;
import me.joelsoep.ctf.cosmetic.CosmeticsManager;
import me.joelsoep.ctf.cosmetic.type.CosmeticType;
import me.joelsoep.ctf.economy.Transaction;
import me.joelsoep.ctf.economy.event.TransactionCancellationEvent;
import me.joelsoep.ctf.economy.event.TransactionCompleteEvent;
import me.joelsoep.ctf.economy.event.TransactionCreateEvent;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.MM;
import me.joelsoep.ctf.util.SoundUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Locale;
import java.util.UUID;

public class StoreListener implements Listener {

    @EventHandler
    public void onCreate(TransactionCreateEvent event) {
        Transaction transaction = event.getTransaction();
        UUID userId = transaction.getUserId();
        CTFPlayer player = PlayerService.getPlayer(userId);
        if (player == null) {
            return;
        }

        player.sendMessage(MM.deserialize("<gray>Creating new order..."));
        announceDebug(player, transaction);
    }

    @EventHandler
    public void onCancel(TransactionCancellationEvent event) {
        Transaction transaction = event.getTransaction();
        CTFPlayer player = PlayerService.getPlayer(transaction.getUserId());
        if (player == null) {
            return;
        }

        player.sendMessage(MM.deserialize("<gray>Your transaction has been cancelled."));
        player.sendMessage(MM.deserialize("<gray>Reason: %s".formatted(event.getReason())));
        announceDebug(player, transaction);
    }

    @EventHandler
    public void onComplete(TransactionCompleteEvent event) {
        Transaction transaction = event.getTransaction();
        CTFPlayer player = PlayerService.getPlayer(transaction.getUserId());
        if (player == null) {
            return;
        }
        String[] itemString = transaction.getItemName().split("\\.");
        String prefix = itemString[0];

        if (!prefix.toUpperCase(Locale.ROOT).equals("COSMETIC")) {
            player.sendMessage(MM.announce("<red>The item you bought was not of initial type COSMETIC! Please report this to an administrator!!"));
            player.sendMessage(MM.announce("<red>Please specify the following ID: %s".formatted(transaction.getUuid().toString())));
            LogModule.ECONOMY.log("transaction with uuid %s was not with prefix cosmetic!!!!!!!".formatted(transaction.getUuid().toString()));
            return;
        }
        String typeString = itemString[1];
        String name = itemString[2];
        CosmeticType type = CosmeticType.valueOf(typeString.toUpperCase(Locale.ROOT));

        Cosmetic cosmetic = Main.getCosmeticsManager().getCosmetic(type, name);
        if (cosmetic == null) {
            player.sendMessage(MM.deserialize("An error occurred while trying to give you your item. Please rejoin the server."));
        } else {
            player.getData().getOwnedCosmetics().add(cosmetic);
        }
        player.sendMessage(MM.deserialize("<green>Completed transaction."));
        player.playSound(SoundUtils.getRewardSound());
        announceDebug(player, transaction);
    }

    private void announceDebug(CTFPlayer player, Transaction transaction) {
        if (!player.isElevated()) {
            return;
        }

        player.sendMessage(MM.deserialize("""
                <gray>Transaction UUID: %s
                <gray>User ID: %s
                <gray>Item: %s
                <gray>Amount: %s
                <gray>State: %s
                <gray>Reason: %s
                """.formatted(
                transaction.getUuid().toString(), transaction.getUserId().toString(), transaction.getItemName(),
                transaction.getAmount(), transaction.getState().toString(), transaction.getReason()
        )));
    }
}
