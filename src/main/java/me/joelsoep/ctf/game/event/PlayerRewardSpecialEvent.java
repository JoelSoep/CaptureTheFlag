package me.joelsoep.ctf.game.event;

import lombok.Getter;
import me.joelsoep.ctf.player.CTFPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class PlayerRewardSpecialEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final CTFPlayer player;

    public PlayerRewardSpecialEvent(CTFPlayer player) {
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
