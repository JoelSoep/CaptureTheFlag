package me.joelsoep.ctf.game.setting.handler;

import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.event.GameStartEvent;
import me.joelsoep.ctf.game.setting.GameSetting;
import me.joelsoep.ctf.game.setting.GameSettingImpl;
import me.joelsoep.ctf.game.setting.GameSettingsManager;
import me.joelsoep.ctf.map.Map;
import me.joelsoep.ctf.map.MapManager;
import me.joelsoep.ctf.map.type.GameMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class MapGameSettingImpl implements GameSettingImpl<GameStartEvent>, Listener {

    @Override
    public GameSetting getSetting() {
        return GameSetting.MAP;
    }

    @Override
    @EventHandler(priority = EventPriority.LOW)
    public void handle(GameStartEvent event) {
        Game game = event.getGame();
        GameSettingsManager manager = game.getGameSettings();
        if (manager.getTotalVotesFor(GameSetting.MAP) == 0) {
            return;
        }

        GameMap map = (GameMap) manager.getSetting(GameSetting.MAP);
        MapManager.registerMapToGame(map, game);
    }
}
