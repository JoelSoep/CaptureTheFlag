package me.joelsoep.ctf.game.event;

import lombok.Getter;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.team.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class FlagReturnEvent extends Event {
    public static final HandlerList handlers = new HandlerList();

    @Getter private final Game game;
    @Getter private final Team flagOwner;
    @Getter private final Player flagHolder;

    public FlagReturnEvent(Game game, Team flagOwner, Player flagHolder) {
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
