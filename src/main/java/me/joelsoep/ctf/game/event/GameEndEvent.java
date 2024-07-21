package me.joelsoep.ctf.game.event;

import lombok.Getter;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.team.Team;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class GameEndEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Game game;
    private final Team winner;
    private final Team loser;

    public GameEndEvent(Game game, Team winner, Team loser) {
        this.game = game;
        this.winner = winner;
        this.loser = loser;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
