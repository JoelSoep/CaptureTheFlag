package me.joelsoep.ctf.game.event;

import lombok.Getter;
import me.joelsoep.ctf.player.CTFPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class PlayerLevelUpEvent extends Event {
    public static final HandlerList handlers = new HandlerList();

   private final CTFPlayer player;
   private final Integer oldLevel;
   private final Integer newLevel;

    public PlayerLevelUpEvent(CTFPlayer player, Integer oldLevel, Integer newLevel) {
        this.player = player;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
