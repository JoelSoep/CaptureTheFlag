package me.joelsoep.ctf.economy;

import lombok.Getter;
import lombok.Setter;
import me.joelsoep.ctf.store.StoreItem;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.UUID;

@Getter
public class Transaction {
    private final @NotNull UUID uuid;
    private final @NotNull UUID userId;
    private final @NotNull String itemName;
    private final int amount;
    private final @NotNull String reason;
    private final @NotNull Date date;

   @Setter
   private TransactionState state;
   @Setter
   private boolean deducted;

    public Transaction(@NotNull UUID userId, @NotNull String itemName, int amount, @NotNull String reason) {
        this.uuid = UUID.randomUUID();
        this.date = new Date();
        this.userId = userId;
        this.itemName = itemName;
        this.amount = amount;
        this.reason = reason;
        this.state = TransactionState.PENDING;
        this.deducted = false;
    }

    public Transaction(@NotNull UUID userId, @NotNull UUID uuid, @NotNull String itemName, int amount, @NotNull Date date, @NotNull String reason) {
        this.uuid = uuid;
        this.userId = userId;
        this.itemName = itemName;
        this.amount = amount;
        this.date = date;
        this.reason = reason;
        this.state = TransactionState.PENDING;
        this.deducted = false;
    }

    public static Transaction fromDocument(Document document) {
        UUID uuid = UUID.fromString(document.getString("_id"));
        UUID userId = UUID.fromString(document.getString("user_id"));
        String itemName = document.getString("item_name");
        int amount = document.getInteger("amount");
        String reason = document.getString("reason");
        Date date = document.getDate("date");
        TransactionState state = TransactionState.valueOf(document.getString("state"));
        boolean deducted = document.getBoolean("deducted");

        Transaction transaction = new Transaction(userId, uuid, itemName, amount, date, reason);
        transaction.setState(state);
        transaction.setDeducted(deducted);
        return transaction;
    }

    public boolean isCancelled() {
        return this.state == TransactionState.CANCELLED;
    }

    public boolean isPending() {
        return this.state == TransactionState.PENDING;
    }

    public boolean isCompleted() {
        return this.state == TransactionState.COMPLETED;
    }
}
