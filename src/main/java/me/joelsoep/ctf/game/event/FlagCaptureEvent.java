package me.joelsoep.ctf.game.event;

import lombok.Getter;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.map.MapLoader;
import me.joelsoep.ctf.game.team.Team;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class FlagCaptureEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    @Getter private final Game game;
    @Getter private final CTFPlayer ctfPlayer;
    @Getter private final Team flagOwner;
    @Getter private final MapLoader mapLoader;

    public FlagCaptureEvent(Game game, CTFPlayer ctfPlayer, Team flagOwner, MapLoader mapLoader) {
        this.game = game;
        this.ctfPlayer = ctfPlayer;
        this.flagOwner = flagOwner;
        this.mapLoader = mapLoader;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
