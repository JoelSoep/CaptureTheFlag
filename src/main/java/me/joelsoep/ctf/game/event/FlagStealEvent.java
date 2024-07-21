package me.joelsoep.ctf.game.event;

import lombok.Getter;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.player.CTFPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class FlagStealEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Game game;
    private final CTFPlayer stealer;
    private final Team flagOwner;

    public FlagStealEvent(Game game, CTFPlayer stealer, Team flagOwner) {
        this.game = game;
        this.stealer = stealer;
        this.flagOwner = flagOwner;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
