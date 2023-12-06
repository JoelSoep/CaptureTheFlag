package me.joelsoep.ctf.game.event;

import lombok.Getter;
import me.joelsoep.ctf.CTFPlayer;
import me.joelsoep.ctf.game.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerLeaveGameEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter private final Game game;
    @Getter private final CTFPlayer player;

    public PlayerLeaveGameEvent(Game game, CTFPlayer player) {
        this.game = game;
        this.player = player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
