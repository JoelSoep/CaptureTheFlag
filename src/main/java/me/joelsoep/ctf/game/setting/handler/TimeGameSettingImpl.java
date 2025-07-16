package me.joelsoep.ctf.game.setting.handler;

import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.event.GameStartEvent;
import me.joelsoep.ctf.game.setting.GameSetting;
import me.joelsoep.ctf.game.setting.GameSettingImpl;
import me.joelsoep.ctf.game.setting.GameSettingsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class TimeGameSettingImpl implements GameSettingImpl<GameStartEvent>, Listener {

    @Override
    public GameSetting getSetting() {
        return GameSetting.TIME;
    }

    @Override
    @EventHandler(priority = EventPriority.LOW)
    public void handle(GameStartEvent event) {
        Game game = event.getGame();
        GameSettingsManager manager = game.getGameSettings();
        long time = (Long) manager.getSetting(getSetting()) * 60L * 1000L;
        game.setGameTime(time);
    }
}
