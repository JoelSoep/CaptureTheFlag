package me.joelsoep.ctf.game.setting;

import me.joelsoep.ctf.game.event.PlayerRespawnEvent;
import org.bukkit.event.Event;

public interface GameSettingImpl<T extends Event> {

    GameSetting getSetting();

    void handle(T event);
}
