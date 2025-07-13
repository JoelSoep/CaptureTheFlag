package me.joelsoep.ctf.game.event;

import lombok.Getter;
import me.joelsoep.ctf.game.setting.GameSetting;
import me.joelsoep.ctf.player.CTFPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class PlayerVoteSettingEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final @NotNull CTFPlayer player;
    private final @NotNull GameSetting setting;
    private final @Nullable Object value;

    public PlayerVoteSettingEvent(@NotNull CTFPlayer player, @NotNull GameSetting setting, @Nullable Object value) {
        this.player = player;
        this.setting = setting;
        this.value = value;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
