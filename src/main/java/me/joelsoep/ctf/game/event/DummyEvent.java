package me.joelsoep.ctf.game.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This is a dummy event class.
 * To be used in case an inherited class that requires a listener,
 * but doesn't have to listen to an event.
 */

public class DummyEvent extends Event {
    public static final HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
