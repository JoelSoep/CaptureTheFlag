package me.joelsoep.ctf.economy.event;

import lombok.Getter;
import me.joelsoep.ctf.economy.Transaction;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TransactionCancellationEvent extends Event {
    public static HandlerList handlers = new HandlerList();

    @Getter private final Transaction transaction;
    @Getter private final String reason;

    public TransactionCancellationEvent(Transaction transaction, String reason) {
        this.transaction = transaction;
        this.reason = reason;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
