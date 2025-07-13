package me.joelsoep.ctf.game.setting.handler;

import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.event.GameEndEvent;
import me.joelsoep.ctf.game.event.GameStartEvent;
import me.joelsoep.ctf.game.setting.GameSetting;
import me.joelsoep.ctf.game.setting.GameSettingImpl;
import me.joelsoep.ctf.game.setting.GameSettingsManager;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class MaxHealthGameSettingImpl implements GameSettingImpl<GameStartEvent>, Listener {

    @Override
    public GameSetting getSetting() {
        return GameSetting.MAX_HEALTH;
    }

    @Override
    @EventHandler(priority = EventPriority.LOW)
    public void handle(GameStartEvent event) {
        Game game = event.getGame();

        GameSettingsManager manager = game.getGameSettings();
        int maxHealth = (int) manager.getSetting(getSetting());

        this.setMaxHealth(game, maxHealth);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void reset(GameEndEvent event) {
        Game game = event.getGame();
        int std = (int) this.getSetting().getDefault();
        this.setMaxHealth(game, std);
    }

    private void setMaxHealth(Game game, double maxHealth) {
        game.getPlayers().forEach(player -> {
            Player bukkitPlayer = player.getPlayer();
            if (bukkitPlayer == null) {
                return;
            }

            AttributeInstance instance = bukkitPlayer.getAttribute(Attribute.MAX_HEALTH);
            if (instance == null) {
                bukkitPlayer.registerAttribute(Attribute.MAX_HEALTH);
                instance = bukkitPlayer.getAttribute(Attribute.MAX_HEALTH); // refresh after
            }

            instance.setBaseValue(maxHealth); // how tf could this be null
        });
    }
}
