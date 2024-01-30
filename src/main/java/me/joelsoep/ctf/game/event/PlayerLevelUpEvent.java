package me.joelsoep.ctf.game.event;

import lombok.Getter;
import me.joelsoep.ctf.player.CTFPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerLevelUpEvent extends Event {
    public static final HandlerList handlers = new HandlerList();

   @Getter  private final CTFPlayer player;
   @Getter private final Integer level;

    public PlayerLevelUpEvent(CTFPlayer player, Integer level) {
        this.player = player;
        this.level = level;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
