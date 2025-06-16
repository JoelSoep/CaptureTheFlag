package me.joelsoep.ctf.economy.event;

import lombok.Getter;
import me.joelsoep.ctf.economy.Transaction;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TransactionCompleteEvent extends Event {
    public static HandlerList handlers = new HandlerList();

    @Getter private final Transaction transaction;

    public TransactionCompleteEvent(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
