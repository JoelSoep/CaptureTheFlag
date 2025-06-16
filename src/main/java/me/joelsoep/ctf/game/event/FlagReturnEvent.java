package me.joelsoep.ctf.game.event;

import lombok.Getter;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.player.CTFPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class FlagReturnEvent extends Event {
    public static final HandlerList handlers = new HandlerList();

    private final Game game;
    private final Team flagOwner;
    private final CTFPlayer flagHolder;

    public FlagReturnEvent(Game game, Team flagOwner, CTFPlayer flagHolder) {
        this.game = game;
        this.flagOwner = flagOwner;
        this.flagHolder = flagHolder;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
