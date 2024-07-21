package me.joelsoep.ctf.game.event;

import lombok.Getter;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.player.CTFPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class PlayerLeaveGameEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Game game;
    private final CTFPlayer player;

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
