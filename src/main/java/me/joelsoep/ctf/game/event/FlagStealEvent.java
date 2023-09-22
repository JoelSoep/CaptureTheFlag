package me.joelsoep.ctf.game.event;

import lombok.Getter;
import me.joelsoep.ctf.CTFPlayer;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.map.MapLoader;
import me.joelsoep.ctf.game.team.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class FlagStealEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter private final Game game;
    @Getter private final CTFPlayer stealer;
    @Getter private final Team flagOwner;
    @Getter private final MapLoader mapLoader;

    public FlagStealEvent(Game game, CTFPlayer stealer, Team flagOwner, MapLoader mapLoader) {
        this.game = game;
        this.stealer = stealer;
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
