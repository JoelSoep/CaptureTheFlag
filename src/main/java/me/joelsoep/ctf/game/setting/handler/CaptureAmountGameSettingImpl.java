package me.joelsoep.ctf.game.setting.handler;

import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.event.GameStartEvent;
import me.joelsoep.ctf.game.setting.GameSetting;
import me.joelsoep.ctf.game.setting.GameSettingImpl;
import me.joelsoep.ctf.game.setting.GameSettingsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CaptureAmountGameSettingImpl implements GameSettingImpl<GameStartEvent>, Listener {

    @Override
    public GameSetting getSetting() {
        return GameSetting.CAPTURE_AMOUNT;
    }

    @Override
    @EventHandler
    public void handle(GameStartEvent event) {
        Game game = event.getGame();
        GameSettingsManager manager = game.getGameSettings();
        int amount = (int) manager.getSetting(getSetting());
        game.setMaxPoints(amount);
    }
}
