package me.joelsoep.ctf.game.event;

import lombok.Getter;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.player.CTFPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class FlagCaptureEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    @Getter private final Game game;
    @Getter private final CTFPlayer ctfPlayer;
    @Getter private final Team flagOwner;

    public FlagCaptureEvent(Game game, CTFPlayer ctfPlayer, Team flagOwner) {
        this.game = game;
        this.ctfPlayer = ctfPlayer;
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
